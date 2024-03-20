package com.jbp.common.kqbill.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class KqRefundQueryResult implements Serializable {

    /**
     * 原交易类型
     */
    private String payTypeDesc;

    /**
     * 退款订单号
     */
    private String orderId;

    /**
     * 退款金额
     */
    private String amount;

    /**
     * 0代表进行中，1代表成功，2代表失败	非必填	1
     */
    private String txnStatus;
}
