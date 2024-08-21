package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
public class OrderInfo {

    private String txn_seqno;

    private String txn_time;  // 格式：yyyyMMddHHmmss。

    private BigDecimal total_amount; // 单位元

    private BigDecimal fee_amount;    // 单位元。会自动收取到平台商户的自有资金账户。金额设定支持低于订单总金额的20%，或者低于10元但不高于订单金额。
}
