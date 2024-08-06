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
@ApiModel(value = "TeamPageRequest对象", description = "团队管理分页对象")
public class TeamPageRequest implements Serializable {

    @ApiModelProperty("团队名称")
    private String name;

    @ApiModelProperty("团队领导账号")
    private String account;

    @ApiModelProperty("团队领导昵称")
    private String nickname;

}
