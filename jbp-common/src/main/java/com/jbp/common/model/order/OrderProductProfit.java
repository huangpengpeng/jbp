package com.jbp.common.model.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_order_product_profit")
@ApiModel(value="OrderProductProfit对象", description="订单商品收益")
public class OrderProductProfit extends BaseModel {

    public OrderProductProfit(Integer orderId, String orderNo, Integer productId, Integer profitType, String profitName, String rule, String postscript) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.productId = productId;
        this.profitType = profitType;
        this.profitName = profitName;
        this.rule = rule;
        this.postscript = postscript;
        this.status = Constants.成功.name();
    }

    public static enum Constants {
        成功, 退回
    }

    @ApiModelProperty(value = "订单ID")
    private Integer orderId;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "收益类型")
    private Integer profitType;

    @ApiModelProperty(value = "收益名称")
    private String profitName;

    @ApiModelProperty(value = "收益规则")
    private String rule;

    @ApiModelProperty(value = "收益附言")
    private String postscript;

    @ApiModelProperty(value = "状态 成功, 退回")
    private String status;

    @ApiModelProperty(value = "备注")
    private String remark;

}
