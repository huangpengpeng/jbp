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
@ApiModel(value="UserMonthActiveResponse对象", description="用户月活跃响应对象")
public class UserMonthActiveResponse implements Serializable {

    @ApiModelProperty(value = "本月是否活跃")
    private Boolean isActive;

    @ApiModelProperty("相差金额")
    private BigDecimal subPrice;

    @ApiModelProperty("复购金额")
    private BigDecimal payPrice;

    @ApiModelProperty("展示信息")
    private String msg;
}
