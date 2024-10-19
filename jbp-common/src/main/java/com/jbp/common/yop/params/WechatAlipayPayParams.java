package com.jbp.common.yop.params;

import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.yop.BaseYopRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class WechatAlipayPayParams extends BaseYopRequest {

    public WechatAlipayPayParams(String orderId, BigDecimal orderAmount, String notifyUrl, String payWay, String channel,
                                 String appId, String openId, String userIp, String scene, String fundProcessType) {
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.notifyUrl = notifyUrl;
        this.fundProcessType = fundProcessType;
        this.expiredTime = DateTimeUtils.format(DateTimeUtils.addHours(new Date(), 72), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN);
        this.payWay = payWay;
        this.channel = channel;
        this.scene = scene;
        this.userIp = userIp;
        this.appId = appId;
        this.userId = openId;
    }

    private String parentMerchantNo;




    @NotBlank(message = "商品名称不能为空")
    private String goodsName;

    @NotBlank(message = "商户编号不能为空")
    private String merchantNo;

    @NotBlank(message = "支付单号不能为空")
    private String orderId;

    @NotNull(message = "支付金额不能为空(保留2位小数单位元)")
    private BigDecimal orderAmount;

    private String notifyUrl;

    /**
     * DELAY_SETTLE:需要分账
     * REAL_TIME:不需要分账
     */
    private String fundProcessType;

    @NotBlank(message = "支付方式不能为空")
    private String payWay;

    @NotBlank(message = "渠道类型不能为空")
    private String channel;

    /**
     * 场景
     * ONLINE:线上
     * OFFLINE:线下
     * BAOXIAN:保险
     * 渠道类型为银联时，请勿传值
     * 示例值：OFFLINE
     */
    @NotBlank(message = "支付场景不能为空")
    private String scene;

    private String appId;

    private String userId;

    @NotBlank(message = "用户IP不能为空")
    private String userIp;

    private String expiredTime;


    /**
     * 先下单后调用聚合支付
     */
    private String token;

    private String uniqueOrderNo;

    public static enum PAYWAY {
        USER_SCAN, //用户扫码
        MINI_PROGRAM, //小程序支付
        WECHAT_OFFIACCOUNT, //:微信公众号
        ALIPAY_LIFE, //:支付宝生活号
        JS_PAY, //:JS支付
        SDK_PAY, //:SDK支付
        H5_PAY //:H5支付
    }

    public static enum CHANNEL {
        WECHAT, //:微信
        ALIPAY, //:支付宝
        UNIONPAY //:银联云闪付
    }

}
