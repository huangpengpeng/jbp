package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.lianlian.result.LztTransferResult;
import com.jbp.common.lianlian.result.QueryWithdrawalResult;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztTransfer;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.model.agent.LztWithdrawal;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.agent.LztTransferDao;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztTransferService;
import com.jbp.service.util.StringUtils;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztTransferServiceImpl extends ServiceImpl<LztTransferDao, LztTransfer> implements LztTransferService {

    @Resource
    private LztAcctService lztAcctService;
    @Resource
    private LztService lztService;
    @Resource
    private MerchantService merchantService;


    @Override
    public LztTransfer create(String payerId, String txnSeqno, BigDecimal amt,
                           String payeeType, String bankAcctNo, String bankCode, String bankAcctName, String cnapsCode,
                           String txnPurpose, String pwd, String random_key, String postscript, String ip) {

        if (StringUtils.isEmpty(txnSeqno)) {
            txnSeqno = com.jbp.service.util.StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通外部代发.getPrefix());
        }
        if (getByTxnSeqno(txnSeqno) != null) {
            throw new CrmebException("转账单号已经被使用");
        }
        LztAcct lztAcct = lztAcctService.getByUserId(payerId);
        Merchant merchant = merchantService.getById(lztAcct.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        BigDecimal feeAmount = lztAcctService.getFee(payerId, amt);
        amt = amt.add(feeAmount);
        LztTransferResult transferResult = lztService.transfer(payInfo.getOidPartner(), payInfo.getPriKey(), payerId, txnPurpose, txnSeqno,
                amt.toString(), feeAmount.toString(), pwd, random_key, payeeType, bankAcctNo, bankCode, bankAcctName,
                cnapsCode, postscript, ip, merchant.getPhone(), merchant.getCreateTime(), merchant.getFrmsWareCategory());
        LztTransfer lztTransfer = new LztTransfer(merchant.getId(), payerId, lztAcct.getUsername(), txnSeqno, transferResult.getAccp_txno(), amt, feeAmount, payeeType, bankAcctNo,
                bankCode, bankAcctName, cnapsCode, postscript);
        lztTransfer.setOrderRet(transferResult);
        save(lztTransfer);
        return lztTransfer;
    }

    @SneakyThrows
    @Override
    public LztTransfer refresh(String txnSeqno) {
        LztTransfer lztTransfer = getByTxnSeqno(txnSeqno);
        if (lztTransfer == null) {
            return null;
        }
        if (LianLianPayConfig.TxnStatus.交易成功.getName().equals(lztTransfer.getTxnStatus())) {
            return lztTransfer;
        }
        Merchant merchant = merchantService.getById(lztTransfer.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        QueryWithdrawalResult result = lztService.queryWithdrawal(payInfo.getOidPartner(), payInfo.getPriKey(), txnSeqno);
        if(result != null && result.getTxn_status() != null){
            if (LianLianPayConfig.TxnStatus.交易成功.getCode().equals(result.getTxn_status())) {
                lztTransfer.setFinishTime(DateTimeUtils.parseDate(result.getFinish_time(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
            }
            lztTransfer.setTxnStatus(LianLianPayConfig.TxnStatus.getName(result.getTxn_status()));
            lztTransfer.setQueryRet(result);
            updateById(lztTransfer);
        }
        return lztTransfer;
    }

    @Override
    public LztTransfer detail(Long id) {
        LztTransfer lztTransfer = getById(id);
        lztTransfer = refresh(lztTransfer.getTxnSeqno());
        return lztTransfer;
    }

    @Override
    public LztTransfer getByTxnSeqno(String txnSeqno) {
        return getOne(new QueryWrapper<LztTransfer>().lambda().eq(LztTransfer::getTxnSeqno, txnSeqno));
    }

    @Override
    public PageInfo<LztTransfer> pageList(Integer merId, String payerId, String txnSeqno, String bankAcctNo,
                                          String bankAcctName, String status, Date startTime, Date endTime,
                                          PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<LztTransfer> lqw = new LambdaQueryWrapper<LztTransfer>()
                .select(LztTransfer.class, info -> !info.getColumn().equals("receipt_zip"))
                .eq(merId != null && merId > 0,  LztTransfer::getMerId, merId)
                .eq(StringUtils.isNotEmpty(status), LztTransfer::getTxnStatus, status)
                .eq(StringUtils.isNotEmpty(payerId), LztTransfer::getPayerId, payerId)
                .eq(StringUtils.isNotEmpty(txnSeqno), LztTransfer::getTxnSeqno, txnSeqno)
                .eq(StringUtils.isNotEmpty(bankAcctNo), LztTransfer::getBankAcctNo, bankAcctNo)
                .eq(StringUtils.isNotEmpty(bankAcctName), LztTransfer::getBankAcctName, bankAcctName)
                .ge(startTime != null, LztTransfer::getCreateTime, startTime)
                .le(endTime != null, LztTransfer::getCreateTime, endTime)
                .orderByDesc(LztTransfer::getId);
        Page<LztTransfer> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<LztTransfer> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> merIdList = list.stream().map(LztTransfer::getMerId).collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        list.forEach(s -> {
            Merchant merchant = merchantMap.get(s.getMerId());
            if (merchant == null) {
                s.setMerName("平台");
            } else {
                s.setMerName(merchant.getName());
                s = refresh(s.getTxnSeqno());
            }
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public LztTransfer check(Long id, String checkReturn, String checkReason) {
        LztTransfer lztTransfer = getById(id);
        LztAcct lztAcct = lztAcctService.getByUserId(lztTransfer.getPayerId());
        Merchant merchant = merchantService.getById(lztAcct.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        lztService.withdrawalCheck(payInfo.getOidPartner(), payInfo.getPriKey(), lztTransfer.getTxnSeqno(),
                lztTransfer.getAmt().toString(), checkReturn, checkReason, lztTransfer.getFeeAmount().toString());
        lztTransfer.setTxnStatus("已复核");
        updateById(lztTransfer);
        return getById(id);
    }

    @Override
    public List<LztTransfer> getWaitDownloadList() {
        QueryWrapper<LztTransfer> q = new QueryWrapper<>();
        q.last("where receipt_token is not null and receipt_token !='' and ( receipt_zip is null or receipt_zip ='') ");
        return list(q);
    }
}
