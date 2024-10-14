package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserJdResponse对象", description="用户京东账号对象")
public class UserJdResponse {
    @ApiModelProperty(value = "uid")
    private Integer uid;
    @ApiModelProperty(value = "账户")
    private String account;
    @ApiModelProperty(value = "团队")
    private String teamName;
    @ApiModelProperty(value = "用户名")
    private String nickname;
    @ApiModelProperty(value = "京东账号")
    private String xid;
}
