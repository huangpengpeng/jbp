package com.jbp.service.service;

import com.jbp.common.yop.result.AccountBalanceQueryResult;
import com.jbp.common.yop.result.BankAccountBalanceQueryResult;
import com.jbp.common.yop.result.BankAccountQueryResult;

public interface YopService {

    // 开户进度查询
    BankAccountQueryResult bankAccountQuery(String merchantNo, String requestNo);

    // 查询银行余额
    BankAccountBalanceQueryResult bankAccountBalanceQuery(String merchantNo, String bankCode, String accountNo);

    // 查询资金账户余额
    AccountBalanceQueryResult accountBalanceQuery(String merchantNo);


}
