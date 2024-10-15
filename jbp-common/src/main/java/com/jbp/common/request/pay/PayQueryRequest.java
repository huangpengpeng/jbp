package com.jbp.common.request.pay;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class PayQueryRequest implements Serializable {

    @NotEmpty(message = "商户秘钥不能为空")
    private String appKey;

    @NotEmpty(message = "当前时间戳不能为空")
    private String timeStr;

    @NotEmpty(message = "调用方法不能为空")
    private String method;

    @NotEmpty(message = "支付签名不能为空")
    private String sign;

    @NotEmpty(message = "交易单号不能为空")
    private String txnSeqno;

}
