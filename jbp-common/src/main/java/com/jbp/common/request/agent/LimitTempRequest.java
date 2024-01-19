package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LimitTempRequest对象", description = "限制模版对象")
public class LimitTempRequest {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "商品显示  商品购买  装修显示")
    private String type;
}
