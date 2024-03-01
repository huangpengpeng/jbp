package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.lianlian.result.QueryWithdrawalResult;
import com.jbp.common.lianlian.result.WithdrawalResult;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztTransferMorepyee;
import com.jbp.common.model.agent.LztWithdrawal;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.model.merchant.MerchantPayInfo;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.agent.LztWithdrawalDao;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.LztService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztWithdrawalService;
import com.jbp.service.util.StringUtils;
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
public class LztWithdrawalServiceImpl extends ServiceImpl<LztWithdrawalDao, LztWithdrawal> implements LztWithdrawalService {
    @Resource
    private LztService lztService;
    @Resource
    private LztAcctService lztAcctService;
    @Resource
    private MerchantService merchantService;

    @Override
    public LztWithdrawal getByTxnSeqno(String txnSeqno) {
        return getOne(new QueryWrapper<LztWithdrawal>().lambda().eq(LztWithdrawal::getTxnSeqno, txnSeqno));
    }

    @Override
    public LztWithdrawal getByAccpTxno(String accpTxno) {
        return getOne(new QueryWrapper<LztWithdrawal>().lambda().eq(LztWithdrawal::getAccpTxno, accpTxno));
    }

    @Override
    public LztWithdrawal withdrawal(Integer merId, String userId, String drawNo, BigDecimal amt, String postscript, String password, String random_key, String ip) {
        if (getByTxnSeqno(drawNo) != null) {
            throw new CrmebException("提现单号已经被使用");
        }
        LztAcct lztAcct = lztAcctService.getByUserId(userId);
        Merchant merchant = merchantService.getById(lztAcct.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        String notifyUrl = "/api/publicly/payment/callback/lianlian/lzt/" + drawNo;
        WithdrawalResult orderResult = lztService.withdrawal(payInfo.getOidPartner(), payInfo.getPriKey(), userId, drawNo,
                amt, postscript, password, random_key, ip, notifyUrl);
        BigDecimal feeAmount = orderResult.getFee_amount() == null ? BigDecimal.ZERO : BigDecimal.valueOf(orderResult.getFee_amount());
        LztWithdrawal withdrawal = new LztWithdrawal(merId, userId, lztAcct.getUsername(), drawNo, orderResult.getAccp_txno(), amt, feeAmount, postscript, orderResult);
        save(withdrawal);
        return withdrawal;
    }

    @Override
    public void callBack(QueryWithdrawalResult result) {
        String accpTxno = result.getAccp_txno();
        LztWithdrawal withdrawal = getByAccpTxno(accpTxno);
        if (withdrawal == null) {
            return;
        }
        if (LianLianPayConfig.TxnStatus.交易成功.getName().equals(withdrawal.getTxnStatus())) {
            return;
        }
        if(result.getTxn_status().equals(LianLianPayConfig.TxnStatus.交易成功.getCode())){
            withdrawal.setFinishTime(DateTimeUtils.parseDate(result.getFinish_time()));
        }
        withdrawal.setTxnStatus(LianLianPayConfig.TxnStatus.getName(result.getTxn_status()));
        withdrawal.setQueryRet(result);
        updateById(withdrawal);
    }

    @Override
    public void refresh(String accpTxno) {
        LztWithdrawal withdrawal = getByAccpTxno(accpTxno);
        if (withdrawal == null) {
            return;
        }
        if (LianLianPayConfig.TxnStatus.交易成功.getName().equals(withdrawal.getTxnStatus())) {
            return;
        }
        Merchant merchant = merchantService.getById(withdrawal.getMerId());
        MerchantPayInfo payInfo = merchant.getPayInfo();
        QueryWithdrawalResult result = lztService.queryWithdrawal(payInfo.getOidPartner(), payInfo.getPriKey(), accpTxno);
        callBack(result);
    }

    @Override
    public PageInfo<LztWithdrawal> pageList(Integer merId, String userId, String txnSeqno, String accpTxno, String status, Date startTime, Date endTime, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<LztWithdrawal> lqw = new LambdaQueryWrapper<LztWithdrawal>()
                .eq(LztWithdrawal::getMerId, merId)
                .eq(StringUtils.isNotEmpty(status), LztWithdrawal::getTxnStatus, status)
                .eq(StringUtils.isNotEmpty(userId), LztWithdrawal::getUserId, userId)
                .eq(StringUtils.isNotEmpty(txnSeqno), LztWithdrawal::getTxnSeqno, txnSeqno)
                .eq(StringUtils.isNotEmpty(accpTxno), LztWithdrawal::getAccpTxno, accpTxno)
                .ge(startTime != null, LztWithdrawal::getCreateTime, startTime)
                .le(endTime != null, LztWithdrawal::getCreateTime, endTime)
                .orderByDesc(LztWithdrawal::getId);
        Page<LztWithdrawal> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<LztWithdrawal> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> merIdList = list.stream().map(LztWithdrawal::getMerId).collect(Collectors.toList());
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
}
