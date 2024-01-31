package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserRelationFlowRequest对象", description = "销售上下层级关系请求对象")
public class UserRelationFlowRequest {
    @ApiModelProperty("用户ID账号")
    private String uAccount;

    @ApiModelProperty("邀请上级账号")
    private String pAccount;

    @ApiModelProperty("层级")
    private Integer level;
}
