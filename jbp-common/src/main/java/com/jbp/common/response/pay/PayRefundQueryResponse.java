package com.jbp.common.response.pay;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PayRefundQueryResponse对象", description="支付订单退款查询结果")
public class PayRefundQueryResponse {

    private String appKey; // 应用appKey

    private String txnSeqno; // 商户系统唯一订单号

    private String platformTxno; // 支付平台订单唯一编号

    private String orderAmt; // 支付金额

    private String txnTime; // 下单时间

    private String successTime; // 成功时间

    /**
     * 成功：SUCCESS
     * 失败：FAIL
     * 处理中：PROCESSING
     */
    private String status; // 状态
}
