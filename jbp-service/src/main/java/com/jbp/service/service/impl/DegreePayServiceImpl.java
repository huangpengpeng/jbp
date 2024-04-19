package com.jbp.service.service.impl;

import com.beust.jcommander.internal.Lists;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.result.*;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztPayChannel;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.yop.constants.YopEnums;
import com.jbp.common.yop.dto.FundBillFlowDto;
import com.jbp.common.yop.result.AccountBalanceQueryResult;
import com.jbp.common.yop.result.BankAccountBalanceQueryResult;
import com.jbp.common.yop.result.FundBillFlowQueryResult;
import com.jbp.service.service.DegreePayService;
import com.jbp.service.service.LztService;
import com.jbp.service.service.YopService;
import com.jbp.service.service.agent.LztPayChannelService;
import com.jbp.service.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class DegreePayServiceImpl implements DegreePayService {

    @Resource
    private LztPayChannelService lztPayChannelService;
    @Resource
    private LztService lztService;
    @Resource
    private YopService yopService;


    @Override
    public AcctInfoResult queryAcct(LztAcct lztAcct) {
        AcctInfoResult acctInfoResult = new AcctInfoResult();
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        if (StringUtils.equals(lztAcct.getPayChannelType(), "连连")) {
            acctInfoResult = lztService.queryAcct(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(), lztAcct.getUserId(), LianLianPayConfig.UserType.getCode(lztAcct.getUserType()));
        }
        if (StringUtils.equals(lztAcct.getPayChannelType(), "易宝")) {
            AccountBalanceQueryResult result = yopService.accountBalanceQuery(lztAcct.getUserId());
            if (result != null && result.validate()) {
                List<AcctInfo> acctinfoList = Lists.newArrayList();
                AcctInfo acctInfo = new AcctInfo();
                acctInfo.setAcct_type("USEROWN_AVAILABLE");
                acctInfo.setAcct_state(result.getAccountStatus().equals("AVAILABLE") ? "NORMAL" : "CANCEL");
                acctInfo.setAmt_balcur(result.getBalance());
                acctInfo.setAmt_balaval(result.getBalance());
                acctInfoResult.setAcctinfo_list(acctinfoList);
            }
        }
        return acctInfoResult;
    }



    @Override
    public LztQueryAcctInfoResult queryBankAcct(LztAcct lztAcct) {
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        LztQueryAcctInfoResult result = new LztQueryAcctInfoResult();
        if (lztAcct.getPayChannelType().equals("连连")) {
            result = lztService.queryBankAcct(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(), lztAcct.getUserId());
        }
        if (lztAcct.getPayChannelType().equals("易宝")) {
            BankAccountBalanceQueryResult yopResult = yopService.bankAccountBalanceQuery(lztAcct.getUserId(), lztAcct.getOpenBank(),
                    lztAcct.getBankAccount());
            if (yopResult != null && yopResult.validate()) {
                List<LztQueryAcctInfo> list = getAcctInfoList(lztAcct, yopResult);
                result.setList(list);
            }
        }
        return result;
    }

    @Override
    public AcctSerialResult queryAcctSerial(LztAcct lztAcct, String startTime, String entTime, Integer pageNo) {
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        Date start = DateTimeUtils.parseDate(startTime);
        Date end = DateTimeUtils.parseDate(entTime);
        AcctSerialResult result = new  AcctSerialResult();
        if (lztAcct.getPayChannelType().equals("连连")) {
            result = lztService.queryAcctSerial(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(), lztAcct.getUserId(),
                    LianLianPayConfig.UserType.getCode(lztAcct.getUserType()), startTime, entTime, null, pageNo.toString());
        }
        if (lztAcct.getPayChannelType().equals("易宝")) {
             FundBillFlowQueryResult yopResult = yopService.fundBillFlowQuery(DateTimeUtils.format(start, DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN),
                    DateTimeUtils.format(end, DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN), lztAcct.getUserId(), pageNo, 10);

            if (yopResult != null && yopResult.validate()) {
                result.setUser_id(lztAcct.getUserId());
                result.setPage_no(pageNo);
                result.setTotal_num(Integer.valueOf(yopResult.getTotalCount()));
                result.setTotal_page(result.getTotal_num()/10);
                List<AcctBalList> list = Lists.newArrayList();
                if(CollectionUtils.isNotEmpty(yopResult.getData())){
                    for (FundBillFlowDto bill : yopResult.getData()) {
                        AcctBalList acctBal = new AcctBalList();
                        acctBal.setTxn_time(DateTimeUtils.format(DateTimeUtils.parseDate(bill.getTrxTime()), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2) );
                        acctBal.setUserId(bill.getMerchantNo());
                        acctBal.setUsername(bill.getMerchantName());
                        acctBal.setUserType(LianLianPayConfig.UserType.getCode(lztAcct.getUserType()));
                        AcctSerialDetailResult detail = new AcctSerialDetailResult();
                        // 收入金额(元)
                        if(StringUtils.isNotEmpty(bill.getIncome()) && ArithmeticUtils.gt(new BigDecimal(bill.getIncome()), BigDecimal.ZERO)){
                            acctBal.setFlag_dc("CREDIT");
                            detail.setOther_acct(bill.getPayerAccountNo());
                            detail.setOther_acct_name(bill.getPayerAccountName());
                        }
                        // 支出金额(元)
                        if(StringUtils.isNotEmpty(bill.getExpenditure()) && ArithmeticUtils.gt(new BigDecimal(bill.getExpenditure()), BigDecimal.ZERO)){
                            acctBal.setFlag_dc("DEBIT");
                            detail.setOther_acct(bill.getPayeeAccountNo());
                            detail.setOther_acct_name(bill.getPayeeAccountName());
                        }
                        // 手续费
                        if(StringUtils.isNotEmpty(bill.getFee()) && ArithmeticUtils.gt(new BigDecimal(bill.getFee()), BigDecimal.ZERO)){
                            acctBal.setFeeAmount(new BigDecimal(bill.getFee()));
                        }
                        // 交易后余额
                        acctBal.setAmt(bill.getOrderAmount());
                        acctBal.setAmt_bal(bill.getBalance());
                        acctBal.setAccp_txnno(bill.getOrderId());
                        acctBal.setMemo(bill.getTradeDesc());
                        acctBal.setTxn_type(YopEnums.TrxCodeEnum.getByValue(bill.getTrxCode()).name());
                        acctBal.setDetail(detail);
                    }
                }
                result.setAcctbal_list(list);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String startTime = "20240419";
        System.out.println(11/10);
    }



    private static List<LztQueryAcctInfo> getAcctInfoList(LztAcct lztAcct, BankAccountBalanceQueryResult result) {
        List<LztQueryAcctInfo> list = Lists.newArrayList();
        LztQueryAcctInfo lztQueryAcctInfo = new LztQueryAcctInfo();
        lztQueryAcctInfo.setAcct_stat("NORMAL");
        lztQueryAcctInfo.setBank_acct_name(lztAcct.getUsername());
        lztQueryAcctInfo.setBank_acct_no(lztAcct.getBankAccount());
        lztQueryAcctInfo.setBank_acct_name(lztAcct.getOpenBank());
        lztQueryAcctInfo.setBank_acct_balance(result.getUseableAmt());
        lztQueryAcctInfo.setBank_acct_frz_balance(result.getFrozenAmt());
        list.add(lztQueryAcctInfo);
        return list;
    }
}
