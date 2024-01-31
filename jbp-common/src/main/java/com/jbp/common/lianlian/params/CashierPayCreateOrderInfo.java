package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class CashierPayCreateOrderInfo {
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
