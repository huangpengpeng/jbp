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
@ApiModel(value = "UserScoreSearchRequest对象", description = "用户分数查询请求对象")
public class UserScoreSearchRequest implements Serializable {

    @ApiModelProperty("用户账号")
    private String account;

    @ApiModelProperty("用户昵称")
    private String nickname;

    @ApiModelProperty("用户昵称")
    private String phone;
}
