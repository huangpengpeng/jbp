package com.jbp.common.lianlian.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode
public class PayMethods {

    /**
     * WECHAT_NATIVE	微信扫码
     * WECHAT_H5	微信H5
     * WECHAT_WXA	微信小程序
     * ALIPAY_NATIVE	支付宝扫码
     * ALIPAY_APP	支付宝APP
     * ALIPAY_H5	支付宝H5
     */
    private String method;
    private BigDecimal amount;

}
