package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderProductStatementResponse对象",description = "商品报表")
public class OrderProductStatementResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品Id")
    private Integer productId;

    @ApiModelProperty(value = "商品条码")
    private String barCode;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "销售数量")
    private Integer salesNum;

    @ApiModelProperty(value = "销售总价")
    private BigDecimal salesPrice;

    @ApiModelProperty(value = "周期")
    private String date;
}
