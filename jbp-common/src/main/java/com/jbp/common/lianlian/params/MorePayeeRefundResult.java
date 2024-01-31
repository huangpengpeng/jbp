package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 退款申请 响应参数
 */
@Data
@EqualsAndHashCode
public class MorePayeeRefundResult {
    private String ret_code;
    private String ret_msg;
    private String oid_partner;
    private String user_id;
    private String txn_seqno;
    private Double total_amount;
    private String accp_txno;
}
