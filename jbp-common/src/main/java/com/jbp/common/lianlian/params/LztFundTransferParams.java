package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LztFundTransferParams {

    public LztFundTransferParams(String txn_seqno, String user_id, String bank_account_no, String amt, String notify_url) {
        this.txn_seqno = txn_seqno;
        this.user_id = user_id;
        this.bank_account_no = bank_account_no;
        this.amt = amt;
        this.notify_url = notify_url;
    }

    /**
     * 交易服务器时间戳格式：yyyyMMddHHmmss
     */
    private String timestamp;

    /**
     * ACCP系统分配给平台商户的唯一编号
     */
    private String oid_partner;

    /**
     * 商户系统唯一交易流水号
     */
    private String txn_seqno;

    private String notify_url;

    /**
     * 商户系统交易时间格式：yyyyMMddHHmmss
     */
    private String txn_time;

    /**
     * 入账用户号外部用户号,连连账户
     */
    private String user_id;

    /**
     * 付款账户号
     */
    private String bank_account_no;

    /**
     * 调拨金额 单位为元，精确到小数点后两位
     */
    private String amt;
}
