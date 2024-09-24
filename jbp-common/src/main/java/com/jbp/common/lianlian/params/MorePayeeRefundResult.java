package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 退款申请 响应参数
 */
@Data
@EqualsAndHashCode
public class MorePayeeRefundResult {

    public MorePayeeRefundResult() {
    }

    public MorePayeeRefundResult(String ret_code, String ret_msg, String oid_partner, String user_id, String txn_seqno, Double total_amount, String accp_txno) {
        this.ret_code = ret_code;
        this.ret_msg = ret_msg;
        this.oid_partner = oid_partner;
        this.user_id = user_id;
        this.txn_seqno = txn_seqno;
        this.total_amount = total_amount;
        this.accp_txno = accp_txno;
    }

    private String ret_code;
    private String ret_msg;
    private String oid_partner;
    private String user_id;
    private String txn_seqno;
    private Double total_amount;
    private String accp_txno;
}
