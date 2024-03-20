package com.jbp.common.kqbill.params;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class KqRefundParams {

    /**
     * 提交此退款的11位平台会员编号 规则：membercode
     */
    private String merchantAcctId;

    private String txnType="bill_drawback_api_1";

    /**
     * 退货金额，单位：分	必填	100
     */
    private String amount;

    /**
     * 商户发送交易请求时的系统时间
     */
    private String entryTime;

    /**
     * 原商家订单号
     */
    private String orgOrderId;
}
