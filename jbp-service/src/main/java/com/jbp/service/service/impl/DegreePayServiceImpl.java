package com.jbp.service.service.impl;

import com.beust.jcommander.internal.Lists;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.params.QueryPaymentOrderInfo;
import com.jbp.common.lianlian.params.QueryPaymentPayeeInfo;
import com.jbp.common.lianlian.params.QueryPaymentPayerInfo;
import com.jbp.common.lianlian.result.*;
import com.jbp.common.model.agent.LztAcct;
import com.jbp.common.model.agent.LztPayChannel;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.yop.constants.YopEnums;
import com.jbp.common.yop.dto.FundBillFlowDto;
import com.jbp.common.yop.result.*;
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
        return result;
    }

    @Override
    public LztFundTransferResult fundTransfer(LztAcct lztAcct, String txnSeqno, String bankAccountNo, String amt, String notifyUrl) {
        LztFundTransferResult result = new LztFundTransferResult();
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        if (lztAcct.getPayChannelType().equals("连连")) {
            result = lztService.fundTransfer(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(),
                    txnSeqno, lztAcct.getUserId(), bankAccountNo, amt, notifyUrl);
        }
        if (lztAcct.getPayChannelType().equals("易宝")) {
            AccountRechargeResult yopResult = yopService.accountRecharge(lztAcct.getUserId(), txnSeqno, amt, lztAcct.getOpenBank(), lztAcct.getBankAccount());
            if (yopResult != null && yopResult.validate()) {
                result.setRet_code("0000");
                result.setRet_msg("交易成功");
                result.setTxn_seqno(txnSeqno);
                result.setAccp_txno(yopResult.getOrderNo());
            }
        }
        return result;
    }

    @Override
    public LztQueryFundTransferResult queryFundTransfer(LztAcct lztAcct, String txnSeqno) {
        LztQueryFundTransferResult result = new LztQueryFundTransferResult();
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        if (lztAcct.getPayChannelType().equals("连连")) {
            result = lztService.queryFundTransfer(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(), lztAcct.getUserId(), txnSeqno);
        }
        if (lztAcct.getPayChannelType().equals("易宝")) {
            AccountRechargeQueryResult yopResult = yopService.accountRechargeQuery(lztAcct.getUserId(), txnSeqno);
            if (yopResult != null && yopResult.validate()) {
                result.setRet_code("0000");
                result.setAccp_txno(yopResult.getOrderNo());
                result.setAmt(yopResult.getOrderAmount());
                if ("INIT".equals(yopResult.getStatus())) {
                    result.setTxn_status("CREATE");
                }
                if ("ACCOUNTING_EXCEPTION".equals(yopResult.getStatus()) || "FAIL".equals(yopResult.getStatus()) || "CANCELED".equals(yopResult.getStatus())) {
                    result.setTxn_status("FAIL");
                }
                if ("ACCOUNTING".equals(yopResult.getStatus())) {
                    result.setTxn_status("PROCESS");
                }
                if ("SUCCESS".equals(yopResult.getStatus())) {
                    result.setTxn_status("SUCCESS");
                }
            }
        }
        return result;
    }

    @Override
    public LztTransferResult transfer(LztAcct lztAcct, String txnPurpose, String txn_seqno,
                                      String amt, String feeAmt, String pwd, String random_key, String payee_type, String bank_acctno,
                                      String bank_code, String bank_acctname, String cnaps_code, String postscript, String ip) {
        LztTransferResult result = new LztTransferResult();
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        if (lztAcct.getPayChannelType().equals("连连")) {
             result = lztService.transfer(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(), lztAcct.getUserId(), txnPurpose, txn_seqno,
                    amt, feeAmt, pwd, random_key, payee_type, bank_acctno, bank_code, bank_acctname,
                     cnaps_code, postscript, ip, lztAcct.getPhone(), lztAcct.getGmtCreated(), lztPayChannel.getFrmsWareCategory());
        }
        if (lztAcct.getPayChannelType().equals("易宝")) {
            if ("BANKACCT_PRI".equals(payee_type)) {
                payee_type = "DEBIT_CARD";
            }
            if ("BANKACCT_PUB".equals(payee_type)) {
                payee_type = "PUBLIC_CARD";
            }
            yopService.accountPayOrder(lztAcct.getUserId(), txn_seqno,
                    amt, bank_acctname, bank_acctno, bank_code, payee_type, cnaps_code, null);
        }
        return result;
    }

    @Override
    public QueryWithdrawalResult transferQuery(LztAcct lztAcct, String txnSeqno) {
        QueryWithdrawalResult result = new QueryWithdrawalResult();
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        if (lztAcct.getPayChannelType().equals("连连")) {
            result = lztService.queryWithdrawal(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(), txnSeqno);
        }
        if (lztAcct.getPayChannelType().equals("易宝")) {
            AccountPayOrderQueryResult yopResult = yopService.accountPayOrderQuery(lztAcct.getUserId(), txnSeqno);
            if (yopResult != null && yopResult.validate()) {
                result.setLinked_acctno(yopResult.getReceiverBankCode());
                if (StringUtils.isNotEmpty(yopResult.getOrderTime())) {
                    result.setAccounting_date(DateTimeUtils.format(DateTimeUtils.parseDate(yopResult.getOrderTime()), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
                }
                if (StringUtils.isNotEmpty(yopResult.getFinishTime())) {
                    result.setFinish_time(DateTimeUtils.format(DateTimeUtils.parseDate(yopResult.getFinishTime()), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
                }
                result.setAccp_txno(yopResult.getOrderNo());

                QueryWithdrawalOrderInfo orderInfo = new QueryWithdrawalOrderInfo();
                orderInfo.setTxn_seqno(yopResult.getRequestNo());
                if (StringUtils.isNotEmpty(yopResult.getOrderAmount())) {
                    orderInfo.setTotal_amount(Double.valueOf(yopResult.getOrderAmount()));
                }
                if (StringUtils.isNotEmpty(yopResult.getFee())) {
                    orderInfo.setFee_amount(Double.valueOf(yopResult.getFee()));
                }
                orderInfo.setTxn_time(yopResult.getOrderTime());
                result.setOrderInfo(orderInfo);

                QueryWithdrawalPayerInfo payerInfo = new QueryWithdrawalPayerInfo();
                payerInfo.setPayer_id(yopResult.getMerchantNo());
                payerInfo.setPayer_type("USER");
                result.setPayerInfo(payerInfo);
                if ("REQUEST_RECEIVE".equals(yopResult.getStatus()) || "REQUEST_ACCEPT".equals(yopResult.getStatus())) {
                    result.setTxn_status("TRADE_WAIT_PAY");
                }
                if ("REMITING".equals(yopResult.getStatus())) {
                    result.setTxn_status("TRADE_PREPAID");
                }
                if ("SUCCESS".equals(yopResult.getStatus())) {
                    result.setTxn_status("TRADE_SUCCESS");
                }
                if ("CANCELED".equals(yopResult.getStatus()) || "FAIL".equals(yopResult.getStatus())) {
                    result.setTxn_status("TRADE_FAILURE");
                }
            }
        }

        return result;
    }


    @Override
    public TransferMorepyeeResult transferMorepyee(LztAcct lztAcct, String orderNo, Double amt, String txnPurpose, String pwd, String randomKey, String payeeId, String ip, String notify_url) {
        TransferMorepyeeResult result = new TransferMorepyeeResult();
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        if (lztAcct.getPayChannelType().equals("连连")) {
            result = lztService.transferMorepyee(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(),
                    lztAcct.getUserId(), orderNo, amt.doubleValue(), txnPurpose, pwd, randomKey, payeeId, ip, notify_url, lztAcct.getPhone(), lztAcct.getGmtCreated(), lztPayChannel.getFrmsWareCategory());
        }
        if (lztAcct.getPayChannelType().equals("易宝")) {
            AccountTransferOrderResult yopResult = yopService.transferB2bOrder(orderNo, lztAcct.getUserId(), payeeId, amt.toString(), null);
            if(yopResult != null && yopResult.validate()){
                result.setAccounting_date(DateTimeUtils.format(new Date(), DateTimeUtils.DEFAULT_DATE_FORMAT_PATTERN2));
                result.setTxn_seqno(yopResult.getRequestNo());
                result.setAccp_txno(yopResult.getOrderNo());
                result.setTotal_amount(Double.valueOf(yopResult.getOrderAmount()));
                result.setRet_code("0000");
            }
        }
        return result;
    }

    @Override
    public QueryPaymentResult queryTransferMorepyee(LztAcct lztAcct, String txnSeqno) {
        QueryPaymentResult result = new QueryPaymentResult();
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        if (lztAcct.getPayChannelType().equals("连连")) {
            result = lztService.queryTransferMorepyee(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(), txnSeqno);
        }
        if (lztAcct.getPayChannelType().equals("易宝")) {
            AccountTransferOrderQueryResult yopResult = yopService.transferB2bOrderQuery(lztAcct.getUserId(), txnSeqno);
            if (yopResult != null && yopResult.validate()) {
                if (StringUtils.isNotEmpty(yopResult.getFinishTime())) {
                    result.setFinish_time(DateTimeUtils.format(DateTimeUtils.parseDate(yopResult.getFinishTime()), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
                }
                if ("REQUEST_RECEIVE".equals(yopResult.getTransferStatus())) {
                    result.setTxn_status("TRADE_WAIT_PAY");
                }
                if ("SUCCESS".equals(yopResult.getTransferStatus())) {
                    result.setTxn_status("TRADE_SUCCESS");
                }
                if ("FAIL".equals(yopResult.getTransferStatus())) {
                    result.setTxn_status("TRADE_CLOSE");
                }
                if ("WAIT_AUDIT".equals(yopResult.getTransferStatus())) {
                    result.setTxn_status("TRADE_WAIT_PAY");
                }
                result.setTxn_type("INNER_FUND_EXCHANGE");
                QueryPaymentOrderInfo paymentOrderInfo = new QueryPaymentOrderInfo();

                paymentOrderInfo.setTxn_seqno(yopResult.getRequestNo());
                paymentOrderInfo.setTotal_amount(Double.valueOf(yopResult.getOrderAmount()));
                paymentOrderInfo.setTxn_time(yopResult.getCreateTime());
                result.setOrderInfo(paymentOrderInfo);

                QueryPaymentPayerInfo payerInfo = new QueryPaymentPayerInfo();
                payerInfo.setPayer_type("USER");
                payerInfo.setPayer_id(lztAcct.getUserId());
                payerInfo.setMethod(yopResult.getTransferType());
                payerInfo.setAmount(Double.valueOf(yopResult.getOrderAmount()));
                result.setPayerInfo(Lists.newArrayList(payerInfo));

                QueryPaymentPayeeInfo payeeInfo = new QueryPaymentPayeeInfo();
                payeeInfo.setAmount(yopResult.getOrderAmount());
                payeeInfo.setPayee_id(yopResult.getToMerchantNo());
                payeeInfo.setPayee_type("USER");
                result.setPayeeInfo(Lists.newArrayList(payeeInfo));
            }
        }
        return result;
    }


    @Override
    public WithdrawalResult withdrawal(LztAcct lztAcct, String drawNo, BigDecimal amt, BigDecimal fee, String postscript, String password, String random_key, String ip, String notifyUrl) {
        WithdrawalResult result = new WithdrawalResult();
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        if (lztAcct.getPayChannelType().equals("连连")) {
            String linked_acctno = "";
            if(LianLianPayConfig.UserType.个人用户.name().equals(lztAcct.getUserType())){
                QueryLinkedAcctResult queryLinkedAcctResult = lztService.queryLinkedAcct(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(), lztAcct.getUserId());
                if(queryLinkedAcctResult == null || CollectionUtils.isEmpty(queryLinkedAcctResult.getLinked_acctlist())){
                    throw new RuntimeException("个人用户提现请先绑定银行卡");
                }
                linked_acctno = queryLinkedAcctResult.getLinked_acctlist().get(0).getLinked_acctno();
            }
            result = lztService.withdrawal(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(), lztAcct.getUserId(), drawNo,
                    amt, fee, postscript, password, random_key, ip, notifyUrl, linked_acctno, lztAcct.getPhone(), lztAcct.getGmtCreated(), lztPayChannel.getFrmsWareCategory());
        }
        if (lztAcct.getPayChannelType().equals("易宝")) {
            WithdrawCardQueryResult card = yopService.withdrawCardQuery(lztAcct.getUserId());
            if (card == null || !card.validate()) {
                throw new RuntimeException("请绑定银行卡信息");
            }
            WithdrawOrderResult yopResult = yopService.withdrawOrder(lztAcct.getUserId(), drawNo, card.getBankCardAccountList().get(0).getBindCardId(), amt.toString(), null);
            if(yopResult != null && yopResult.validate()){
                result.setRet_code("0000");
                result.setUser_id(lztAcct.getUserId());
                result.setTxn_seqno(drawNo);
                result.setAccp_txno(yopResult.getOrderNo());
                result.setTotal_amount(amt.doubleValue());
            }
        }
        return result;
    }

    @Override
    public QueryWithdrawalResult queryWithdrawal(LztAcct lztAcct, String txnSeqno) {
        QueryWithdrawalResult result = new QueryWithdrawalResult();
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        if (lztAcct.getPayChannelType().equals("连连")) {
            result = lztService.queryWithdrawal(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(), txnSeqno);
        }
        if (lztAcct.getPayChannelType().equals("易宝")) {
            WithdrawOrderQueryResult yopResult = yopService.withdrawOrderQuery(lztAcct.getUserId(), txnSeqno);
            if (yopResult != null && yopResult.validate()) {
                result.setLinked_acctno(yopResult.getAccountNo());
                if (StringUtils.isNotEmpty(yopResult.getOrderTime())) {
                    result.setAccounting_date(DateTimeUtils.format(DateTimeUtils.parseDate(yopResult.getOrderTime()), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
                }
                if (StringUtils.isNotEmpty(yopResult.getFinishTime())) {
                    result.setFinish_time(DateTimeUtils.format(DateTimeUtils.parseDate(yopResult.getFinishTime()), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
                }
                result.setAccp_txno(yopResult.getOrderNo());

                QueryWithdrawalOrderInfo orderInfo = new QueryWithdrawalOrderInfo();
                orderInfo.setTxn_seqno(yopResult.getRequestNo());
                if (StringUtils.isNotEmpty(yopResult.getOrderAmount())) {
                    orderInfo.setTotal_amount(Double.valueOf(yopResult.getOrderAmount()));
                }
                if (StringUtils.isNotEmpty(yopResult.getFee())) {
                    orderInfo.setFee_amount(Double.valueOf(yopResult.getFee()));
                }
                orderInfo.setTxn_time(yopResult.getOrderTime());
                result.setOrderInfo(orderInfo);

                QueryWithdrawalPayerInfo payerInfo = new QueryWithdrawalPayerInfo();
                payerInfo.setPayer_id(yopResult.getMerchantNo());
                payerInfo.setPayer_type("USER");
                result.setPayerInfo(payerInfo);
                if ("REQUEST_RECEIVE".equals(yopResult.getStatus()) || "REQUEST_ACCEPT".equals(yopResult.getStatus())) {
                    result.setTxn_status("TRADE_WAIT_PAY");
                }
                if ("REMITING".equals(yopResult.getStatus())) {
                    result.setTxn_status("TRADE_PREPAID");
                }
                if ("SUCCESS".equals(yopResult.getStatus())) {
                    result.setTxn_status("TRADE_SUCCESS");
                }
                if ("CANCELED".equals(yopResult.getStatus()) || "FAIL".equals(yopResult.getStatus())) {
                    result.setTxn_status("TRADE_FAILURE");
                }
            }
        }
        return result;
    }

    @Override
    public ReceiptDownloadResult receiptDownload(LztAcct lztAcct, String receipt_accp_txno, String txnSeqno, String token, String tradeType) {
        ReceiptDownloadResult result = new ReceiptDownloadResult();
        LztPayChannel lztPayChannel = lztPayChannelService.getById(lztAcct.getPayChannelId());
        if (lztAcct.getPayChannelType().equals("连连")) {
            result = lztService.receiptDownload(lztPayChannel.getPartnerId(), lztPayChannel.getPriKey(),
                    receipt_accp_txno, token);
        }
        if (lztAcct.getPayChannelType().equals("易宝")) {
            AccountReceiptResult yopResult = yopService.accountReceiptGet(lztAcct.getUserId(), txnSeqno, tradeType);
            result.setReceipt_sum_file(yopResult.getData());

        }
        return result;
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