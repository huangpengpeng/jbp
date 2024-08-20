package com.jbp.common.model.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_order_scan_pay", autoResultMap = true)
@ApiModel(value = "OrderScanPay对象", description = "订单扫码支付")
public class OrderScanPay extends BaseModel {

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "收款人名称")
    private String payeeName;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "支付方法  微信  支付宝  银行卡")
    private String payMethod;

    @ApiModelProperty(value = "支付方式:weixin,alipay,yue, wallet, lianlian, confirmPay, quickPay")
    private String payType;

    @ApiModelProperty(value = "支付渠道：连连 易宝 ")
    private String payChannel;

    @ApiModelProperty(value = "订单状态（待付款 已付款）")
    private String status;

    @ApiModelProperty(value = "退款状态：未退款  已退款")
    private String refundStatus;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    public static enum Enum {
        微信, 支付宝, 银行卡

    }
}
