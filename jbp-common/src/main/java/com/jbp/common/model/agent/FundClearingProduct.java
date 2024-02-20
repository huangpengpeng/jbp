package com.jbp.common.model.agent;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 佣金发放商品信息
 */
@Data
@NoArgsConstructor
public class FundClearingProduct implements Serializable {

    public FundClearingProduct(Integer productId, String productName, BigDecimal price,
                               Integer quantity, BigDecimal ratio, BigDecimal commAmt) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.ratio = ratio;
        this.commAmt = commAmt;
    }

    @ApiModelProperty("商品ID")
    private Integer productId;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("总价")
    private BigDecimal price;

    @ApiModelProperty("数量")
    private Integer quantity;

    @ApiModelProperty("比例")
    private BigDecimal ratio;

    @ApiModelProperty("佣金")
    private BigDecimal commAmt;
}
