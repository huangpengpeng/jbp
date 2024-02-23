package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserRelationRequest对象", description = "服务关系上下级请求对象")
public class UserRelationRequest implements Serializable {

    @ApiModelProperty("用户ID账号")
    private String uAccount;

    @ApiModelProperty("邀请上级账号")
    private String pAccount;

    @ApiModelProperty("节点")
    private Integer node;
}
