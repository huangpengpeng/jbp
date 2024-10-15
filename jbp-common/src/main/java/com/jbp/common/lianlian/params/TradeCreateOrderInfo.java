package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class TradeCreateOrderInfo {

    public TradeCreateOrderInfo() {
    }

    public TradeCreateOrderInfo(String txn_seqno, String txn_time, Double total_amount, Double fee_amount, String goods_name) {
        this.txn_seqno = txn_seqno;
        this.txn_time = txn_time;
        this.total_amount = total_amount;
        this.fee_amount = fee_amount;
        this.goods_name = goods_name;
    }

    private String txn_seqno;
    private String txn_time;
    private Double total_amount;
    private Double fee_amount;
    private String order_info;
    private String goods_name;
    private String goods_url;
}
