package com.jbp.common.yop.params;

import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.yop.BaseYopRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class TradeOrderParams extends BaseYopRequest {
    public TradeOrderParams(String merchantNo, String orderId, String orderAmount, String goodsName,
                            String notifyUrl, String memo, String redirectUrl) {
        this.merchantNo = merchantNo;
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.goodsName = goodsName;
        this.notifyUrl = notifyUrl;
        this.memo = memo;
        this.expiredTime = DateTimeUtils.format(DateTimeUtils.addHours(new Date(), 72), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN);
        this.redirectUrl = redirectUrl;
    }

    private String parentMerchantNo;

    @NotBlank(message = "商户商编")
    private String merchantNo;

    //商户收款请求号。可包含字母、数字、下划线；需保证在商户端不重复。合单收款场景中，此参数为合单收款请求号
    @NotBlank(message = "订单ID不能为空")
    private String orderId;

    @NotBlank(message = "订单金额不能为空")
    private String orderAmount;

    @NotBlank(message = "商品名称不能为空")
    private String goodsName;

    @NotBlank(message = "回调地址不能为空")
    private String notifyUrl;

    @NotBlank(message = "对账备注不能为空")
    private String memo;

    // 订单过期时间 yyyy-MM-dd HH:mm:ss
    private String expiredTime;

    @NotBlank(message = "支付成功跳转页面地址不能为空")
    private String redirectUrl;

    private String csUrl;

    // 分账方式不能为空， DELAY_SETTLE 默认传分账
    private String fundProcessType;

    private String subOrderDetail;
}
