package com.jbp.service.service;

import com.jbp.common.yop.params.BankAccountOpenParams;
import com.jbp.common.yop.params.OnlineBankOrderParams;
import com.jbp.common.yop.result.*;

import java.io.InputStream;

public interface YopService {

    String upload(String url);

    /**
     * 银行开户
     *
     */
    BankAccountOpenResult bankAccountOpen(BankAccountOpenParams params);


    /**
     * 充值下单
     *
     */
    OnlineBankOrderResult onlineBankOrder(OnlineBankOrderParams params);

    /**
     * 开户进度查询
     */
    BankAccountQueryResult bankAccountQuery(String merchantNo, String requestNo);

    /**
     * 查询银行余额
     */
    BankAccountBalanceQueryResult bankAccountBalanceQuery(String merchantNo, String bankCode, String accountNo);

    /**
     * 查询资金账户余额
     */
    AccountBalanceQueryResult accountBalanceQuery(String merchantNo);

    /**
     * 提现卡bin查询
     */
    WithdrawCardQueryResult withdrawCardQuery(String merchantNo);

    /**
     * 提现下单
     */
    WithdrawOrderResult withdrawOrder(String merchantNo, String requestNo, String bankCardId, String orderAmount, String notifyUrl);

    /**
     * 提现查询
     */
    WithdrawOrderQueryResult withdrawOrderQuery(String merchantNo, String requestNo);

    /**
     * 充值 --- 划拨
     */
    AccountRechargeResult accountRecharge(String merchantNo, String requestNo, String amount, String bankCode, String bankAccountNo);

    /**
     * 充值 --- 划拨 查询
     */
    AccountRechargeQueryResult accountRechargeQuery(String merchantNo, String requestNo);

    /**
     * 转账
     */
    AccountTransferOrderResult transferB2bOrder(String requestNo, String fromMerchantNo, String toMerchantNo, String orderAmount, String notifyUrl);

    /**
     * 转账查询
     */
    AccountTransferOrderQueryResult transferB2bOrderQuery(String merchantNo, String requestNo);

    /**
     * 代付
     *
     */

    AccountPayOrderResult accountPayOrder(String merchantNo, String requestNo,
                                          String orderAmount, String receiverAccountName,
                                          String receiverAccountNo, String receiverBankCode,
                                          String bankAccountType, String branchBankCode, String notifyUrl);

    /**
     * 代付查询
     *
     */
    AccountPayOrderQueryResult accountPayOrderQuery(String merchantNo, String requestNo);

    /**
     * 资金流水
     */
    FundBillFlowQueryResult fundBillFlowQuery(String startDate, String endDate, String merchantNo, Integer page, Integer size);


    /**
     * 回执下载
     */
    AccountReceiptResult accountReceiptGet(String merchantNo, String requestNo, String tradeType);




}
