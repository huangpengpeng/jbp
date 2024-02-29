package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LztFundTransferNotifyParams {

    /**
     * ACCP系统分配给平台商户的唯一编号
     */
    private String oid_partner;

    /**
     * accp的全局唯一号
     */
    private String txn_seqno;

    /**
     * 来账时间	系统交易时间格式：yyyyMMddHHmmss
     */
    private String txn_time;

    /**
     * 入账账户名称
     */
    private String bank_account_name;

    /**
     * 入账账户号
     */
    private String bank_account_no;

    /**
     * 入账账户对应用户号 外部用户号
     */
    private String user_id;

    /**
     * 对手账户号用户在合作银行开通的账号
     */
    private String payer_acctno;

    /**
     * 对手账户名称 用户在合作银行开通的账号名称
     */
    private String payer_acctname;

    /**
     * 金额，单位为元，精确到小数点后两位
     */
    private String amt;

    /**
     * 备注
     */
    private String remark;

}
