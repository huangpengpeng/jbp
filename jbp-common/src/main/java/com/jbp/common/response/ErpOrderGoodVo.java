package com.jbp.common.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ErpOrderGoodVo implements Serializable {

    public ErpOrderGoodVo(String orderDetailId, String goodsName, int quantity, String unit, String productSn, BigDecimal price, BigDecimal consumePrice) {
        this.orderDetailId = orderDetailId;
        this.goodsName = goodsName;
        this.quantity = quantity;
        this.unit = unit;
        this.productSn = productSn;
        this.price = price;
        this.consumePrice = consumePrice;
    }

    @ApiModelProperty(value = "订单详情ID")
    private String orderDetailId;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品数量")
    private int quantity;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "规格编码")
    private String productSn;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "消费单价")
    private BigDecimal consumePrice;
}
