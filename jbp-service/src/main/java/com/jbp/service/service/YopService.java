package com.jbp.service.service;

import com.jbp.common.yop.result.*;

public interface YopService {

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
     * 划拨
     */
    AccountRechargeResult accountRecharge(String merchantNo, String requestNo, String amount, String bankCode, String bankAccountNo, String userRequestIP);


}
