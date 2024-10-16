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
@ApiModel(value="PayCreateResponse对象", description="支付订单创建结果")
public class PayCreateResponse {

    public PayCreateResponse(String appKey, String txnSeqno,  String orderAmt) {
        this.appKey = appKey;
        this.txnSeqno = txnSeqno;
        this.orderAmt = orderAmt;
    }

    private String appKey; // 应用appKey

    private String txnSeqno; // 商户系统唯一订单号

    private String  platformTxno; // 支付平台订单唯一编号

    private String orderAmt; // 支付金额

    private String payload; // 支付信息，js支付信息、二维码信息
}
