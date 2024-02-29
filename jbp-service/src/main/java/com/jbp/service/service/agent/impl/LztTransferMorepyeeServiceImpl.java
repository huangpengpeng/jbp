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
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.agent.LztTransferMorepyeeDao;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.agent.LztTransferMorepyeeService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztTransferMorepyeeServiceImpl extends ServiceImpl<LztTransferMorepyeeDao, LztTransferMorepyee> implements LztTransferMorepyeeService {

    @Resource
    private LianLianPayService lianLianPayService;

    @Override
    public LztTransferMorepyee transferMorepyee(Integer merId, String payerId, String orderNo, BigDecimal amt,
                                                String txnPurpose, String pwd, String randomKey, String payeeId, String ip, String postscript) {
        if (getByTxnSeqno(orderNo) != null) {
            throw new CrmebException("单号已经被使用");
        }
        String notifyUrl = "/api/publicly/payment/callback/lianlian/lzt/"+orderNo;
        TransferMorepyeeResult result = lianLianPayService.lztTransferMorepyee(payerId, orderNo, amt.doubleValue(), txnPurpose, pwd, randomKey, payeeId, ip, notifyUrl);
        LztTransferMorepyee transferMorepyee = new LztTransferMorepyee(merId, payerId, payeeId, orderNo, amt, postscript, result, result.getAccp_txno());
        save(transferMorepyee);
        return transferMorepyee;
    }

    @Override
    public void callBack(QueryPaymentResult paymentResult) {
        String accpTxno = paymentResult.getAccp_txno();
        LztTransferMorepyee transferMorepyee = getByAccpTxno(accpTxno);
        if (transferMorepyee == null) {
            return;
        }
        if (LianLianPayConfig.TxnStatus.交易成功.getName().equals(transferMorepyee.getTxnStatus())) {
            return;
        }
        if(paymentResult.getTxn_status().equals(LianLianPayConfig.TxnStatus.交易成功.getCode())){
            transferMorepyee.setFinishTime(DateTimeUtils.parseDate(paymentResult.getFinish_time()));
        }
        transferMorepyee.setTxnStatus(LianLianPayConfig.TxnStatus.getName(paymentResult.getTxn_status()));
        transferMorepyee.setQueryRet(paymentResult);
        updateById(transferMorepyee);
    }

    @Override
    public void refresh(String accpTxno) {
        LztTransferMorepyee lztTransferMorepyee = getByAccpTxno(accpTxno);
        if (lztTransferMorepyee == null) {
            return;
        }
        if (LianLianPayConfig.TxnStatus.交易成功.getName().equals(lztTransferMorepyee.getTxnStatus())) {
            return;
        }
        QueryPaymentResult result = lianLianPayService.lztQueryTransferMorepyee(accpTxno);
        callBack(result);
    }

    @Override
    public LztTransferMorepyee getByTxnSeqno(String txnSeqno) {
        return getOne(new QueryWrapper<LztTransferMorepyee>().lambda().eq(LztTransferMorepyee::getTxnSeqno, txnSeqno));
    }

    @Override
    public LztTransferMorepyee getByAccpTxno(String accpTxno) {
        return getOne(new QueryWrapper<LztTransferMorepyee>().lambda().eq(LztTransferMorepyee::getAccpTxno, accpTxno));
    }

    @Override
    public PageInfo<LztTransferMorepyee> pageList(Integer merId, String payerId, String payeeId, String txnSeqno, String accpTxno,
                                                  String status, Date startTime, Date endTime, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<LztTransferMorepyee> lqw = new LambdaQueryWrapper<LztTransferMorepyee>()
                .eq(LztTransferMorepyee::getMerId, merId)
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
        return CommonPage.copyPageInfo(page, list);
    }
}
