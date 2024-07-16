package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WechatAlipayTutelagePayParams extends BaseYopRequest {

    public WechatAlipayTutelagePayParams(String parentMerchantNo, String merchantNo, String orderId,
                                         String orderAmount, String goodsName,
                                         String notifyUrl, String payWay,
                                         String channel, String userIp, String token) {
        this.parentMerchantNo = parentMerchantNo;
        this.merchantNo = merchantNo;
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.goodsName = goodsName;
        this.fundProcessType = "REAL_TIME";
        this.notifyUrl = notifyUrl;
        this.payWay = payWay;
        this.channel = channel;
        this.scene = "OFFLINE";
        this.userIp = userIp;
        this.token = token;
    }

    private String parentMerchantNo;

    private String merchantNo;

    private String orderId;

    private String orderAmount;

    private String goodsName;

    private String fundProcessType;

    private String notifyUrl;

    private String payWay;

    private String channel;

    private String scene;

    private String userIp;

    private String token;
}
