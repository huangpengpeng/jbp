package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserInvitationRequest对象", description = "销售上下级关系请求对象")
public class UserInvitationRequest implements Serializable {

    @ApiModelProperty("用户ID账号")
    private String uAccount;

    @ApiModelProperty("邀请上级账号")
    private String pAccount;

    @ApiModelProperty("转挂上级账号")
    private String mAccount;

}
