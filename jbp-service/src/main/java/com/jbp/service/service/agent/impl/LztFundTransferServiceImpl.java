package com.jbp.service.service.agent.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.result.LztFundTransferResult;
import com.jbp.common.lianlian.result.LztQueryAcctInfo;
import com.jbp.common.lianlian.result.LztQueryFundTransferResult;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztFundTransfer;
import com.jbp.common.model.merchant.Merchant;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.agent.LztFundTransferDao;
import com.jbp.service.service.DegreePayService;
import com.jbp.service.service.MerchantService;
import com.jbp.service.service.agent.LztAcctService;
import com.jbp.service.service.agent.LztFundTransferService;
import com.jbp.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztFundTransferServiceImpl extends ServiceImpl<LztFundTransferDao, LztFundTransfer> implements LztFundTransferService {

    @Resource
    private LztAcctService lztAcctService;
    @Resource
    private MerchantService merchantService;
    @Resource
    private DegreePayService degreePayService;
    @Resource
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init(){
        redisTemplate.delete("LztFundTransferTask.send");
    }

    @Override
    public LztFundTransfer fundTransfer(Integer merId, String userId, String bankAccountNo, BigDecimal amt, String postscript) {
        String txnSeqno = StringUtils.N_TO_10(LianLianPayConfig.TxnSeqnoPrefix.来账通划拨资金.getPrefix());
        LztAcct lztAcct = lztAcctService.getByUserId(userId);
        String notifyUrl = "/api/publicly/payment/callback/lianlian/lzt/" + txnSeqno;
        LztFundTransferResult result = degreePayService.fundTransfer(lztAcct, txnSeqno, bankAccountNo, amt.toString(), notifyUrl);
        LztFundTransfer lztFundTransfer = new LztFundTransfer(merId, userId, lztAcct.getUsername(), txnSeqno, amt, DateTimeUtils.getNow(), bankAccountNo, postscript, result.getAccp_txno(), lztAcct.getPayChannelType());
        save(lztFundTransfer);
        return lztFundTransfer;
    }

    @Override
    public LztFundTransfer refresh(String txnSeqno) {
        LztFundTransfer lztFundTransfer = getByTxnSeqno(txnSeqno);
        if (lztFundTransfer == null || lztFundTransfer.getStatus().equals(LianLianPayConfig.FundTransferStatus.成功.name())) {
            return lztFundTransfer;
        }
        LztAcct lztAcct = lztAcctService.getByUserId(lztFundTransfer.getUserId());
        LztQueryFundTransferResult result = degreePayService.queryFundTransfer(lztAcct, txnSeqno);
        lztFundTransfer.setStatus(LianLianPayConfig.FundTransferStatus.getName(result.getTxn_status()));
        lztFundTransfer.setRetMsg(result.getRet_msg());
        updateById(lztFundTransfer);
        return lztFundTransfer;
    }

    @Override
    public LztFundTransfer getByTxnSeqno(String txnSeqno) {
        return getOne(new QueryWrapper<LztFundTransfer>().lambda().eq(LztFundTransfer::getTxnSeqno, txnSeqno));
    }

    @Override
    public PageInfo<LztFundTransfer> pageList(Integer merId, String userId, String username, String bankAccountNo,
                                              String txnSeqno, String accpTxno, Date startTime, Date endTime, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<LztFundTransfer> lqw = new LambdaQueryWrapper<LztFundTransfer>()
                .eq(LztFundTransfer::getMerId, merId)
                .eq(StringUtils.isNotEmpty(userId), LztFundTransfer::getUserId, userId)
                .eq(StringUtils.isNotEmpty(username), LztFundTransfer::getUsername, username)
                .eq(StringUtils.isNotEmpty(bankAccountNo), LztFundTransfer::getBankAccountNo, bankAccountNo)
                .eq(StringUtils.isNotEmpty(txnSeqno), LztFundTransfer::getTxnSeqno, txnSeqno)
                .eq(StringUtils.isNotEmpty(accpTxno), LztFundTransfer::getAccpTxno, accpTxno)
                .ge(startTime != null, LztFundTransfer::getTxnTime, startTime)
                .le(endTime != null, LztFundTransfer::getTxnTime, endTime)
                .orderByDesc(LztFundTransfer::getId);
        Page<FundClearing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<LztFundTransfer> list = list(lqw);
        if (CollectionUtils.isEmpty(list)) {
            return CommonPage.copyPageInfo(page, list);
        }
        List<Integer> merIdList = list.stream().map(LztFundTransfer::getMerId).collect(Collectors.toList());
        Map<Integer, Merchant> merchantMap = merchantService.getMapByIdList(merIdList);
        list.forEach(s -> {
            Merchant merchant = merchantMap.get(s.getMerId());
            if (merchant == null) {
                s.setMerName("平台");
            } else {
                s.setMerName(merchant.getName());

            }
            s = refresh(s.getTxnSeqno());
        });

        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public void autoFundTransfer() {
        Date now = DateTimeUtils.getNow();
        Date startDate = DateTimeUtils.getStartDate(now);
        Date finallyDate = DateTimeUtils.getFinallyDate(now);
        List<LztAcct> lztAcctList = lztAcctService.list(new LambdaQueryWrapper<LztAcct>().eq(LztAcct::getIfOpenBankAcct, true));
        for (LztAcct lztAcct : lztAcctList) {
            log.info("正在执行自动划拨账户:{}, 名称:{}", lztAcct.getUserId(), lztAcct.getUsername());
            List<LztFundTransfer> list = list(new LambdaQueryWrapper<LztFundTransfer>().eq(LztFundTransfer::getUserId, lztAcct.getUserId())
                    .in(LztFundTransfer::getStatus, Lists.newArrayList("处理中", "成功")).between(LztFundTransfer::getGmtCreated, startDate, finallyDate));
            BigDecimal sum = BigDecimal.ZERO;
            BigDecimal max = BigDecimal.valueOf(1000000);
            if ("xwbank".equals(lztAcct.getOpenBank())) {
                max = BigDecimal.valueOf(100000000);
            }
            if (CollectionUtils.isNotEmpty(list)) {
                for (LztFundTransfer lztFundTransfer : list) {
                    sum = sum.add(lztFundTransfer.getAmt());
                }
                if (ArithmeticUtils.gte(sum, max)) {
                    continue;
                }
            }
            BigDecimal usableAmt = max.subtract(sum);
            LztAcct acctDetail = lztAcctService.details(lztAcct.getUserId());
            List<LztQueryAcctInfo> bankAcctInfoList = acctDetail.getBankAcctInfoList();
            if (CollectionUtils.isNotEmpty(bankAcctInfoList)) {
                LztQueryAcctInfo lztBankAcct = bankAcctInfoList.get(0);
                BigDecimal balance = StringUtils.isEmpty(lztBankAcct.getBank_acct_balance()) ? BigDecimal.ZERO : new BigDecimal(lztBankAcct.getBank_acct_balance());
                BigDecimal frzBalance = StringUtils.isEmpty(lztBankAcct.getBank_acct_frz_balance()) ? BigDecimal.ZERO : new BigDecimal(lztBankAcct.getBank_acct_frz_balance());
                balance = balance.subtract(frzBalance);
                log.info("正在执行自动划拨账户余额:{}", balance);
                BigDecimal transferAmt = BigDecimal.valueOf(Math.min(usableAmt.doubleValue(), balance.doubleValue()));
                if (ArithmeticUtils.gt(transferAmt, BigDecimal.ZERO)) {
                    try {
                        LztFundTransfer lztFundTransfer = fundTransfer(lztAcct.getMerId(), lztAcct.getUserId(), lztBankAcct.getBank_acct_no(), transferAmt, "自动划拨");
                        log.info("自动划拨成功:{}", JSONObject.toJSONString(lztFundTransfer));
                    } catch (Exception e) {

                    }
                }
            }
        }
    }
}
