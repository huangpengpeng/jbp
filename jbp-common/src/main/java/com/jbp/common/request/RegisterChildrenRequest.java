package com.jbp.common.request;

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
@ApiModel(value = "RegisterSixRequest对象", description = "后台6个1星注册对象")
public class RegisterChildrenRequest implements Serializable {

    @ApiModelProperty("账号")
    @NotBlank(message = "账号不能为空")
    private String account;

    @ApiModelProperty("昵称")
    @NotBlank(message = "昵称不能为空")
    private String nickname;


}
