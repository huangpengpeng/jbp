package com.jbp.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
public class CbecOrderDetailDto implements Serializable {

    public CbecOrderDetailDto(String name, Integer quantity, BigDecimal price, BigDecimal score, String img) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.score = score;
        this.img = img;
    }

    @ApiModelProperty(value = "产品名称")
    private String name;

    @ApiModelProperty(value = "产品数量")
    private Integer quantity;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "积分")
    private BigDecimal score;

    @ApiModelProperty(value = "图片")
    private String img;
   

}
