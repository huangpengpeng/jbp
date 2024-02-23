package com.jbp.service.product.comm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CommCalculateResult implements Serializable {

    public CommCalculateResult(Integer uid, Integer type, String name, Integer productId, String productName, BigDecimal price,
                               Integer quantity, BigDecimal pv, BigDecimal scale, BigDecimal ratio, BigDecimal amt, Integer sort) {
        this.uid = uid;
        this.type = type;
        this.name = name;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.pv = pv;
        this.scale = scale;
        this.ratio = ratio;
        this.amt = amt;
        this.sort = sort;
    }

    @ApiModelProperty(value = "佣金类型")
    private Integer uid;

    @ApiModelProperty(value = "佣金类型")
    private Integer type;

    @ApiModelProperty(value = "佣金名称")
    private String name;

    @ApiModelProperty(value = "产品ID")
    private Integer productId;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "pv")
    private BigDecimal pv;

    @ApiModelProperty(value = "系数")
    private BigDecimal scale;

    @ApiModelProperty(value = "得奖比例")
    private BigDecimal ratio;

    @ApiModelProperty(value = "得奖金额")
    private BigDecimal amt;

    @ApiModelProperty(value = "顺序")
    private int sort;

}
