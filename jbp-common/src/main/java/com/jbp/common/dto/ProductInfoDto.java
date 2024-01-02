package com.jbp.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ProductInfoDto对象", description="产品信息")
public class ProductInfoDto implements Serializable {

    public ProductInfoDto(Integer productId, String productName, int quantity,
                          BigDecimal price, BigDecimal pv) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.pv = pv;
    }

    @ApiModelProperty("商品ID")
    private Integer productId;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("数量")
    private int quantity;

    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("pv系数")
    private BigDecimal scale;

    @ApiModelProperty("pv值")
    private BigDecimal pv;
}
