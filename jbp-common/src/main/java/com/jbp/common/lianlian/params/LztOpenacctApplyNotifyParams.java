package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LztOpenacctApplyNotifyParams {

    /**
     * 商户号，ACCP系统分配给平台商户的唯一编号
     */
    private String oid_partner;
    /**
     * 用户在商户系统中的唯一编号，要求该编号在商户系统能唯一标识用户。由商户自定义。
     */
    private String user_id;
    /**
     * 商户系统唯一交易流水号
     */
    private String txn_seqno;
    /**
     * ACCP系统交易单号
     */
    private String accp_txno;
    /**
     * ACCP系统用户编号
     */
    private String oid_userno;
    /**
     * 账户状态
     * PENDING:待开户
     * WAIT_SIGN:待开户意愿确认
     * NORMAL:正常
     * FAIL:失败
     * CANCEL:注销
     */
    private String acct_status;

    /**
     * 备注。开户失败时，该字段内容是失败原因。
     */
    private String remark;
    /**
     * 银行卡号。开户成功返回。
     */
    private String bank_acct_no;
    /**
     * 银行编码
     */
    private String bank_code;
    /**
     * 开户行行号
     */
    private String brbank_no;
    /**
     * 法人开户意愿认证链接地址
     */
    private String gateway_url;

}
