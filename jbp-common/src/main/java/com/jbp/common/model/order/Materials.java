package com.jbp.common.model.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Materials对象", description = "产品物料")
public class Materials implements Serializable {

    public Materials(String name, Integer quantity, BigDecimal price, String code, BigDecimal payPrice) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.code = code;
        this.payPrice = payPrice;
    }

    @ApiModelProperty(value = "物料名称")
    private String name;

    @ApiModelProperty(value = "物料数量")
    private Integer quantity;

    @ApiModelProperty(value = "物料成本价")
    private BigDecimal price;

    @ApiModelProperty(value = "物料编码")
    private String code;

    @ApiModelProperty(value = "物料总价")
    private BigDecimal payPrice;
}
