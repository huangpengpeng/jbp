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
@ApiModel(value = "InvitationScoreGroupRequest对象", description = "销售业绩分组请求对象")
public class InvitationScoreGroupRequest implements Serializable {

    @ApiModelProperty("用户账户")
    private String account;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("变动方向")
    private String action;
}