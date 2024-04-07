package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransferOrderInfo {

    /**
     * 商户系统唯一交易流水号。由商户自定义。
     */
    private String txn_seqno;

    /**
     * 商户系统交易时间格式：yyyyMMddHHmmss。
     */
    private String txn_time;

    /**
     * 订单总金额，单位为元，精确到小数点后两位。
     */
    private Double total_amount;
    /**
     * 代发交易用途。
     * 服务费
     * 信息费
     * 修理费
     * 佣金支付
     * 贷款
     * 其他
     */
    private String txn_purpose;

    /**
     * 支持自定义
     * 订单信息，在查询API和支付通知中原样返回，可作为自定义参数使用
     */
    private String order_info;
}
