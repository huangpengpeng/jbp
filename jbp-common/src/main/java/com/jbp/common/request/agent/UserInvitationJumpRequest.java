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
@ApiModel(value = "UserInvitationJumpRequest对象", description = "销售上下级关系请求对象")
public class UserInvitationJumpRequest implements Serializable {
    @ApiModelProperty("用户ID")
    private Integer uId;

    @ApiModelProperty("当前上级")
    private Integer pId;

    @ApiModelProperty("原来上级")
    private Integer orgPid;
}
