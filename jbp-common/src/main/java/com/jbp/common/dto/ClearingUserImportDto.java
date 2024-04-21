package com.jbp.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClearingUserImportDto{

    @ApiModelProperty("账户")
    private String account;

    @ApiModelProperty("级别")
    private Long level;

    @ApiModelProperty("级别名称")
    private String levelName;

    @ApiModelProperty("权重")
    private BigDecimal weight;
}
