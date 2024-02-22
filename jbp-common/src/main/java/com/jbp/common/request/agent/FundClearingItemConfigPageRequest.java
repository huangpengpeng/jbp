package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FundClearingItemConfigPageRequest对象", description = "佣金发放配置列表请求")
public class FundClearingItemConfigPageRequest {
    @ApiModelProperty("佣金名称")
    private String commName;
}
