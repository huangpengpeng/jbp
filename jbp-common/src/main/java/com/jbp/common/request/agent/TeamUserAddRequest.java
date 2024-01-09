package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TeamUserAddRequest对象", description = "团队用户添加对象")
public class TeamUserAddRequest {
    @ApiModelProperty("团队名称")
    private Integer tid;
    @ApiModelProperty("用户账号")
    private String account;
}
