package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class CapaOrderRequest {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty(value = "是否有供货权")
    private Boolean ifSupply;

    @ApiModelProperty(value = "是否向公司订货")
    private Boolean ifCompany;

    @ApiModelProperty(value = "订货金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "补货金额")
    private BigDecimal repAmount;

    @ApiModelProperty(value = "升级图片是否展示")
    private Boolean ifShow;
}
