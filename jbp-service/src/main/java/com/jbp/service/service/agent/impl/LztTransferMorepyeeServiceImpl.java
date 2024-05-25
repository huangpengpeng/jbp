package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.lianlian.result.QueryPaymentResult;
import com.jbp.common.lianlian.result.TransferMorepyeeResult;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.agent.LztTransferMorepyeeDao;
import com.jbp.service.service.DegreePayService;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztTransferMorepyeeService;
import com.jbp.service.util.StringUtils;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztTransferMorepyeeServiceImpl extends ServiceImpl<LztTransferMorepyeeDao, LztTransferMorepyee> implements LztTransferMorepyeeService {

    @Resource
    private LztService lztService;
    @Resource
    private LztAcctService lztAcctService;
    @Resource
    private MerchantService merchantService;
    @Resource
    private DegreePayService degreePayService;

    @Override
    public LztTransferMorepyee transferMorepyee(Integer merId, String payerId, String orderNo, BigDecimal amt, BigDecimal feeAmount,
                                                String txnPurpose, String pwd, String randomKey, String payeeId, String ip, String postscript) {
        if (StringUtils.isEmpty(orderNo)) {
            orderNo = com.jbp.service.util.StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通内部代发.getPrefix());
        }
        if (getByTxnSeqno(orderNo) != null) {
            throw new CrmebException("单号已经被使用");
        }
        LztAcct payerAcct = lztAcctService.getByUserId(payerId);
        if (payerAcct == null) {
            throw new CrmebException("付款账户不存在");
        }
        LztAcct payeeAcct = lztAcctService.getByUserId(payeeId);
        if (payeeAcct == null) {
            throw new CrmebException("收款账户不存在");
        }
        String notifyUrl = "/api/publicly/payment/callback/lianlian/lzt/" + orderNo;
        BigDecimal fee = lztAcctService.getFee(payerId, amt);
        amt = amt.add(fee);
        TransferMorepyeeResult result = degreePayService.transferMorepyee(payerAcct, orderNo, amt.doubleValue(), txnPurpose, pwd, randomKey, payeeId, ip, notifyUrl);
        LztTransferMorepyee transferMorepyee = new LztTransferMorepyee(merId, payerId, payerAcct.getUsername(), payeeId, payeeAcct.getUsername(), orderNo, amt, fee,postscript, result, result.getAccp_txno(), payerAcct.getPayChannelType());
        save(transferMorepyee);
        return transferMorepyee;
    }

    @SneakyThrows
    @Override
    public LztTransferMorepyee callBack(QueryPaymentResult paymentResult) {
        if(paymentResult == null || paymentResult.getOrderInfo() == null ){
            return null;
        }
        String txnSeqno = paymentResult.getOrderInfo().getTxn_seqno();
        if(StringUtils.isEmpty(txnSeqno)){
            return null;
        }
        LztTransferMorepyee transferMorepyee = getByTxnSeqno(txnSeqno);
        if (transferMorepyee == null) {
            return null;
        }
        if (LianLianPayConfig.TxnStatus.交易成功.getName().equals(transferMorepyee.getTxnStatus())) {
            return transferMorepyee;
        }
        if (paymentResult.getTxn_status().equals(LianLianPayConfig.TxnStatus.交易成功.getCode())) {
            transferMorepyee.setFinishTime(DateTimeUtils.parseDate(paymentResult.getFinish_time(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
        }
        transferMorepyee.setTxnStatus(LianLianPayConfig.TxnStatus.getName(paymentResult.getTxn_status()));
        transferMorepyee.setQueryRet(paymentResult);
        updateById(transferMorepyee);
        return transferMorepyee;
    }



    @Override
    public LztTransferMorepyee refresh(String txnSeqno) {
        LztTransferMorepyee lztTransferMorepyee = getByTxnSeqno(txnSeqno);
        if (lztTransferMorepyee == null) {
            return null;
        }
        if (LianLianPayConfig.TxnStatus.交易成功.getName().equals(lztTransferMorepyee.getTxnStatus())) {
            return lztTransferMorepyee;
        }
        LztAcct lztAcct = lztAcctService.getByUserId(lztTransferMorepyee.getPayerId());
        QueryPaymentResult result = degreePayService.queryTransferMorepyee(lztAcct, txnSeqno);
        LztTransferMorepyee update = callBack(result);
        if (update == null) {
            return lztTransferMorepyee;
        }
        return update;
    }

    @Override
    public LztTransferMorepyee getByTxnSeqno(String txnSeqno) {
        return getOne(new QueryWrapper<LztTransferMorepyee>().lambda().eq(LztTransferMorepyee::getTxnSeqno, txnSeqno));
    }


    @Override
    public PageInfo<LztTransferMorepyee> pageList(Integer merId, String payerId, String payeeId, String txnSeqno, String accpTxno,
                                                  String status, Date startTime, Date endTime, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<LztTransferMorepyee> lqw = new LambdaQueryWrapper<LztTransferMorepyee>()
                .select(LztTransferMorepyee.class, info -> !info.getColumn().equals("receipt_zip"))
                .eq(merId!= null && merId > 0,  LztTransferMorepyee::getMerId, merId)
                .eq(StringUtils.isNotEmpty(status), LztTransferMorepyee::getTxnStatus, status)
                .eq(StringUtils.isNotEmpty(payerId), LztTransferMorepyee::getPayerId, payeeId)
                .eq(StringUtils.isNotEmpty(payeeId), LztTransferMorepyee::getPayeeId, payeeId)
                .eq(StringUtils.isNotEmpty(txnSeqno), LztTransferMorepyee::getTxnSeqno, txnSeqno)
                .eq(StringUtils.isNotEmpty(accpTxno), LztTransferMorepyee::getAccpTxno, accpTxno)
                .ge(startTime != null, LztTransferMorepyee::getCreateTime, startTime)
                .le(endTime != null, LztTransferMorepyee::getCreateTime, endTime)
                .orderByDesc(LztTransferMorepyee::getId);
        Page<LztTransferMorepyee> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<LztTransferMorepyee> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> merIdList = list.stream().map(LztTransferMorepyee::getMerId).collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        list.forEach(s -> {
            Merchant merchant = merchantMap.get(s.getMerId());
            if (merchant == null) {
                s.setMerName("平台");
            } else {
                s.setMerName(merchant.getName());
                s=  refresh(s.getTxnSeqno());
            }
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public LztTransferMorepyee detail(Long id) {
        LztTransferMorepyee lztTransferMorepyee = getById(id);
        refresh(lztTransferMorepyee.getTxnSeqno()) ;
        return getById(id);
    }

    @Override
    public List<LztTransferMorepyee> getWaitDownloadList() {
        QueryWrapper<LztTransferMorepyee> q = new QueryWrapper<>();
        q.last("where receipt_token is not null and receipt_token !='' and ( receipt_zip is null or receipt_zip ='') ");
        return list(q);
    }
}
