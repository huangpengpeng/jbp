package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FundClearingFlowGetRequest", description = "佣金发放记录头部数据请求对象")
public class FundClearingFlowGetRequest {

    @ApiModelProperty(value = "头部状态 0:全部奖励 1:待发放 2:已完结 ")
    @NotNull(message = "头部状态不能为空")
    private Integer headerStatus;

}
