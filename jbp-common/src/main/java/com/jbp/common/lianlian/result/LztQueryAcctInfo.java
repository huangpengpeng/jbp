package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LztQueryAcctInfo {

    /**
     * 商户系统唯一交易流水号
     */
    private String txn_seqno;
    /**
     * 账户名
     */
    private String bank_acct_name;
    /**
     * 账户号
     */
    private String bank_acct_no;
    /**
     * 账户状态
     *
     * 账户状态。
     * PENDING:待开户
     * WAIT_SIGN:待开户意愿确认
     * NORMAL:正常
     * FAIL:失败
     * CANCEL:注销
     */
    private String acct_stat;
    /**
     * 账户银行余额bank_acct_balance
     */
    private String bank_acct_balance;

    /**
     * 冻结金额
     */
    private String bank_acct_frz_balance;
    /**
     * 银行编码
     */
    private String bank_code;
    /**
     * 开户行行号
     */
    private String brbank_no;
}
