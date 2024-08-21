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
@TableName(value = "eb_order_pay_record", autoResultMap = true)
@ApiModel(value = "OrderPayRecord对象", description = "订单支付记录")
public class OrderPayRecord extends BaseModel {

    @ApiModelProperty(value = "支付渠道ID")
    private Long orderPayChannelId;

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "收款人名称")
    private String payeeName;

    @ApiModelProperty(value = "类型 商品码  收款码")
    private String type;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "支付单号")
    private String payNo;

    @ApiModelProperty(value = "回执单号")
    private String receiptNo;

    @ApiModelProperty(value = "银联单号")
    private String unionPayNo;

    @ApiModelProperty(value = "订单状态（待付款 已付款, 已取消）")
    private String status;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "支付时间")
    private String payTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "下单结果")
    private String orderResultInfo;

    @ApiModelProperty(value = "查询结果")
    private String queryResultInfo;

    public static enum StatusEnum {
        商品码, 收款码

    }
}
