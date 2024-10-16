package com.jbp.common.lianlian.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class CashierPayCreateOrderInfo {

    public CashierPayCreateOrderInfo(String txn_seqno, String txn_time, Double total_amount, String goods_name) {
        this.txn_seqno = txn_seqno;
        this.txn_time = txn_time;
        this.total_amount = total_amount;
        this.goods_name = goods_name;
    }

    /**
     * 商户系统唯一交易流水号。由商户自定义。
     */
    private String txn_seqno;
    /**
     * 商户系统交易时间。
     * 格式：yyyyMMddHHmmss。
     */
    private String txn_time;
    /**
     * 订单金额
     */
    private Double total_amount;
    /**
     * 商品名称
     */
    private String goods_name;
}
