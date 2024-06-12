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
@ApiModel(value = "UserRelationGplotRequest对象", description = "服务上下层级关系拓扑图请求对象")
public class UserRelationGplotRequest implements Serializable {

    @ApiModelProperty("用户账号")
    private String account;
}
