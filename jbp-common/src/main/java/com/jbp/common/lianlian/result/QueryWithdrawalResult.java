package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 提现申请 响应参数
 */
@Data
@EqualsAndHashCode
public class QueryWithdrawalResult {
    private String ret_code;
    private String ret_msg;
    private String oid_partner;

    /**
     * 个人用户绑定的银行卡号，企业用户绑定的银行帐号。
     */
    private String linked_acctno;

    /**
     * 账务日期。ACCP系统交易账务日期，交易成功时返回，格式：yyyyMMdd
     */
    private String accounting_date;

    /**
     * 支付完成时间，格式为yyyyMMddHHmmss
     */
    private String finish_time;

    /**
     * ACCP系统交易单号。
     */
    private String accp_txno;

    /**
     * 渠道交易单号。
     */
    private String chnl_txno;

    /**
     * 提现交易状态。
     * TRADE_SUCCESS：交易成功
     * TRADE_FAILURE：交易失败
     * TRADE_CANCEL：退汇
     * TRADE_PREPAID：预付完成。
     */
    private String txn_status;

    /**
     * 提现失败原因。当txn_status为FAILURE或CANCEL时返回具体失败原因信息。
     */

    private String failure_reason;
    /**
     * 渠道原始原因。提现失败渠道原始原因。
     */
    private String chnl_reason;
    /**
     * 银行编码，提现收款银行编码。
     */
    private String bankcode;

    /**
     * 付款方信息
     */
    private QueryWithdrawalPayerInfo payerInfo;

    /**
     * 订单信息
     */
    private QueryWithdrawalOrderInfo orderInfo;

}
