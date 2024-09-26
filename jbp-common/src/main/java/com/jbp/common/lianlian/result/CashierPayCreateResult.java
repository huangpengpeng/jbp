package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账户+收银台 响应参数
 */
@Data
@EqualsAndHashCode
public class CashierPayCreateResult {

    public CashierPayCreateResult() {
    }

    public CashierPayCreateResult(String ret_code, String ret_msg, String oid_partner,
                                  String user_id, Double total_amount, String txn_seqno, String accp_txno, String gateway_url, String payload) {
        this.ret_code = ret_code;
        this.ret_msg = ret_msg;
        this.oid_partner = oid_partner;
        this.user_id = user_id;
        this.total_amount = total_amount;
        this.txn_seqno = txn_seqno;
        this.accp_txno = accp_txno;
        this.gateway_url = gateway_url;
        this.payload = payload;
    }

    private String ret_code;
    private String ret_msg;
    private String oid_partner;
    private String user_id;
    private Double total_amount;
    private String txn_seqno;
    private String accp_txno;
    private String gateway_url;
    private String payload;
    private String token;
}
