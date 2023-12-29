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
@AllArgsConstructor
@NoArgsConstructor
public class FundClearingProduct implements Serializable {

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("单价")
    private BigDecimal price;

    @ApiModelProperty("数量")
    private Integer quantity;

    @ApiModelProperty("比例")
    private BigDecimal ratio;

    @ApiModelProperty("佣金")
    private BigDecimal commAmt;
}
