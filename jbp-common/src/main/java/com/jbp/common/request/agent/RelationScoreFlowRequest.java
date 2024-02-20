package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RelationScoreFlowRequest对象", description = "服务业绩明细请求对象")
public class RelationScoreFlowRequest {

    @ApiModelProperty("用户账户")
    private String account;

    @ApiModelProperty("下单用户账户")
    private String orderAccount;
}
