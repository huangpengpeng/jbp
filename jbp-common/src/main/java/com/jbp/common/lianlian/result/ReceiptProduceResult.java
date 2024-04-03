package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ReceiptProduceResult {

    private String oid_partner;

    private String ret_code;

    private String ret_msg;

    private String txn_seqno;

    private String trade_txn_seqno;

    private String trade_accp_txno;

    private String total_amount;

    // 电子回单流水号
    private String receipt_accp_txno;

    // 授权令牌
    private String token;



}
