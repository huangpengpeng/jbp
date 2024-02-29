package com.jbp.common.lianlian.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class QueryWithdrawalOrderInfo {

    /**
     * 商户系统唯一交易流水号
     */
    private String txn_seqno;

    /**
     * 商户系统交易时间格式：yyyyMMddHHmmss
     */
    private String txn_time;

    /**
     * 订单总金额，单位为元，精确到小数点后两位。
     */
    private Double total_amount;

    /**
     * 手续费金额，单位为元，精确到小数点后两位。回显提现接口传的手续费金额。
     */
    private Double fee_amount;

    /**
     * 用于订单说明，透传返回。
     */
    private String order_info;


}
