package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ReceiptProduceParams {

    private String timestamp;

    private String oid_partner;

    /**
     * 商户系统唯一交易流水号。由商户自定义。
     */
    private String txn_seqno;

    /**
     * 格式：yyyyMMddHHmmss。
     */
    private String txn_time;

    /**
     * 原交易流水号
     */
    private String trade_txn_seqno;

    private String trade_bill_type;

    private String total_amount;

    private String memo;

}
