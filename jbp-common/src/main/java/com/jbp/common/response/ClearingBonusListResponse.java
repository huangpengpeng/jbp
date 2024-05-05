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
@ApiModel(value = "ClearingBonusListResponse对象", description = "结算日列表")
public class ClearingBonusListResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "佣金")
    private BigDecimal commAmt;

    @ApiModelProperty(value = "天数")
    private String time;

    @ApiModelProperty(value = "佣金名称")
    private String name;


}
