package com.jbp.common.response.pay;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PayQueryResponse对象", description="支付订单查询结果")
public class PayQueryResponse {

    public PayQueryResponse(String appKey, String method, String txnSeqno, String platformTxno,
                            String orderAmt, String txnTime) {
        this.appKey = appKey;
        this.method = method;
        this.txnSeqno = txnSeqno;
        this.platformTxno = platformTxno;
        this.orderAmt = orderAmt;
        this.txnTime = txnTime;
    }

    private String appKey; // 应用appKey

    private String method; // 支付方法

    private String txnSeqno; // 商户系统唯一订单号

    private String platformTxno; // 支付平台订单唯一编号

    private String orderAmt; // 支付金额

    private String feeAmount;// 交易手续费，单位为元

    private String txnTime; // 下单时间

    private String successTime; // 成功时间

    /**
     * 成功：SUCCESS
     * 失败：FAIL
     * 处理中：PROCESSING
     */
    private String status; // 状态

}
