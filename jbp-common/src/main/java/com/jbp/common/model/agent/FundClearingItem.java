package com.jbp.common.model.agent;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 出款备注
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundClearingItem implements Serializable {

    @ApiModelProperty("出款名称")
    private String name;

    @ApiModelProperty("类型")
    private Integer type;

    @ApiModelProperty("出款金额")
    private BigDecimal amt;
}
