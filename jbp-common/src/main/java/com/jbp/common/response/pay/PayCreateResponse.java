package com.jbp.common.response.pay;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PayCreateResponse对象", description="支付订单创建结果")
public class PayCreateResponse {

    private String appKey; // 应用appKey

    private String txnSeqno; // 商户系统唯一订单号

    private String  platformTxno; // 支付平台订单唯一编号

    private String orderAmt; // 支付金额

    private String payload; // 支付信息，js支付信息、二维码信息
}
