package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class QueryPaymentOrderInfo {
    private String txn_seqno;
    // 商户系统交易时间
    private String txn_time;
    // 订单总金额，单位为元
    private Double total_amount;
    /**
     * 透传字段
     */
    private String order_info;
}
