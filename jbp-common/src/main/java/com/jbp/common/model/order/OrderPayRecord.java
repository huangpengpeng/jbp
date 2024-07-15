package com.jbp.common.model.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_order_pay_record", autoResultMap = true)
@ApiModel(value = "OrderPayRecord对象", description = "订单支付记录")
public class OrderPayRecord extends BaseModel {

    @ApiModelProperty(value = "类型 商城订单  收款码")
    private String type;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "支付方法")
    private String payMethod;

    @ApiModelProperty(value = "支付方式:weixin,alipay,yue, wallet, lianlian, confirmPay, quickPay")
    private String payType;

    @ApiModelProperty(value = "支付渠道：public-公众号,mini-小程序，h5-网页支付,yue-余额，wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付宝，alipayApp-支付宝App, wallet, lianlian")
    private String payChannel;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "支付商户号")
    private String merchantNo;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "支付时间")
    private String payTime;


    public static enum StatusEnum {
        商城订单, 收款码

    }
}
