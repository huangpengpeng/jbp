package com.jbp.common.lianlian.result;

import com.jbp.common.lianlian.params.RefundMethod;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 退款申请 请求参数
 */
@Data
@EqualsAndHashCode
public class RefundQueryResult {
    private String ret_code;
    private String ret_msg;
    private String oid_partner;
    private String accounting_date;
    private String finish_time;
    private String accp_txno;
    private String chnl_txno;
    private Double refund_amount;

    /**
     * 退款交易状态。
     * TRADE_SUCCESS：退款成功
     * TRADE_FAILURE：退款失败
     * TRADE_PROCESSING：退款处理中。
     * 退款结果以此为准，商户按此进行后续业务逻辑处理。
     */
    private String txn_status;

    private RefundMethod[] refundMethods;

}
