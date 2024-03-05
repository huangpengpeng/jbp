package com.jbp.service.service.agent.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.lianlian.result.AcctInfo;
import com.jbp.common.lianlian.result.LztQueryAcctInfo;
import com.jbp.common.lianlian.result.QueryPaymentResult;
import com.jbp.common.lianlian.result.TransferMorepyeeResult;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztAcctOpen;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.agent.LztTransferMorepyeeDao;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztAcctOpenService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztTransferMorepyeeService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
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
    private LztAcctOpenService lztAcctOpenService;

    @Override
    public LztTransferMorepyee transferMorepyee(Integer merId, String payerId, String orderNo, BigDecimal amt,
                                                String txnPurpose, String pwd, String randomKey, String payeeId, String ip, String postscript) {
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
        Merchant merchant = merchantService.getById(payerAcct.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();

        String notifyUrl = "/api/publicly/payment/callback/lianlian/lzt/" + orderNo;
        TransferMorepyeeResult result = lztService.transferMorepyee(payInfo.getOidPartner(), payInfo.getPriKey(),
                payerId, orderNo, amt.doubleValue(), txnPurpose, pwd, randomKey, payeeId, ip, notifyUrl);
        LztTransferMorepyee transferMorepyee = new LztTransferMorepyee(merId, payerId, payerAcct.getUsername(), payeeId, payeeAcct.getUsername(), orderNo, amt, postscript, result, result.getAccp_txno());
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
        if (paymentResult.getTxn_status().equals(LianLianPayConfig.TxnStatus.交易成功.getCode())) {
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
        Merchant merchant = merchantService.getById(lztTransferMorepyee.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        QueryPaymentResult result = lztService.queryTransferMorepyee(payInfo.getOidPartner(), payInfo.getPriKey(), accpTxno);
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
            }
        });
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Map<String, Object> info(Integer merId) {
        Map<String, Object> map = new HashMap<>();
        //入金额
        LambdaQueryWrapper<LztTransferMorepyee> lwq = new LambdaQueryWrapper<LztTransferMorepyee>()
                .eq(ObjectUtil.isNotEmpty(merId), LztTransferMorepyee::getPayeeId, merId)
                .eq(LztTransferMorepyee::getTxnStatus, LianLianPayConfig.TxnStatus.交易成功.getName())
                .apply("TO_DAYS(finish_time) = TO_DAYS(NOW())");
        BigDecimal depositAmount = list(lwq).stream().map(LztTransferMorepyee::getAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        lwq.apply("TO_DAYS(NOW()) - TO_DAYS(finish_time) = 1");
        BigDecimal yesterdayDepositAmount = list(lwq).stream().map(LztTransferMorepyee::getAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        map.put("todayWepositAmount", depositAmount);
        map.put("yesterdayDepositAmount", yesterdayDepositAmount);
        //出金额
        LambdaQueryWrapper<LztTransferMorepyee> lambdaQueryWrapper = new LambdaQueryWrapper<LztTransferMorepyee>()
                .eq(ObjectUtil.isNotEmpty(merId), LztTransferMorepyee::getMerId, merId)
                .eq(LztTransferMorepyee::getTxnStatus, LianLianPayConfig.TxnStatus.交易成功.getName())
                .apply("TO_DAYS(finish_time) = TO_DAYS(NOW())");
        BigDecimal withdrawalAmount = list(lambdaQueryWrapper).stream().map(LztTransferMorepyee::getAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        lambdaQueryWrapper.apply("TO_DAYS(NOW()) - TO_DAYS(finish_time) = 1");
        BigDecimal yesterdayWithdrawalAmount = list(lambdaQueryWrapper).stream().map(LztTransferMorepyee::getAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
        map.put("todayWithdrawalAmount", withdrawalAmount);
        map.put("yesterdayWithdrawalAmount", yesterdayWithdrawalAmount);
        LambdaQueryWrapper<LztAcctOpen> openLambdaQueryWrapper = new LambdaQueryWrapper<LztAcctOpen>()
                .eq(ObjectUtil.isNotEmpty(merId), LztAcctOpen::getMerId, merId)
                .eq(LztAcctOpen::getStatus, LianLianPayConfig.TxnStatus.交易成功.getName())
                .apply("TO_DAYS(txn_time) = TO_DAYS(NOW())");
        int todayCount = lztAcctOpenService.count(openLambdaQueryWrapper);
        openLambdaQueryWrapper.apply("TO_DAYS(NOW()) - TO_DAYS(txn_time) = 1");
        int yesterdayTodayCount = lztAcctOpenService.count(openLambdaQueryWrapper);
        map.put("todayCount", todayCount);
        map.put("yesterdayTodayCount", yesterdayTodayCount);

        //总金额
        LambdaQueryWrapper<LztAcctOpen> lztAcctOpenLambdaQueryWrapper = new LambdaQueryWrapper<LztAcctOpen>()
                .eq(ObjectUtil.isNotEmpty(merId), LztAcctOpen::getMerId, merId)
                .eq(LztAcctOpen::getStatus, LianLianPayConfig.TxnStatus.交易成功.getName());
        List<String> userIdList = lztAcctOpenService.list(lztAcctOpenLambdaQueryWrapper).stream().map(LztAcctOpen::getUserId).collect(Collectors.toList());
        BigDecimal totalAmt=new BigDecimal(0);
        for (String s : userIdList) {
            LztAcct details = lztAcctService.details(s);
            List<AcctInfo> acctInfoList = details.getAcctInfoList();
            for (AcctInfo acctInfo : acctInfoList) {
                totalAmt.add(new BigDecimal(acctInfo.getAmt_balcur()));
            }
            List<LztQueryAcctInfo> bankAcctInfoList = details.getBankAcctInfoList();
            if (CollectionUtils.isNotEmpty(bankAcctInfoList)) {
                for (LztQueryAcctInfo lztQueryAcctInfo : bankAcctInfoList) {
                    totalAmt.add(new BigDecimal(lztQueryAcctInfo.getBank_acct_balance()));
                }
            }
        }
        map.put("totalAmt",totalAmt);
        return map;
    }
}
