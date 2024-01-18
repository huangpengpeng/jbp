package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserHelpRegisterRequest", description = "帮忙注册")
public class UserHelpRegisterRequest implements Serializable {

    @ApiModelProperty(value = "username", required = true)
    @NotBlank(message = "昵称不能为空")
    private String username;

    @ApiModelProperty(value = "pAccount", required = true)
    @NotBlank(message = "销售上级不能为空")
    private String pAccount;

    @ApiModelProperty(value = "rAccount", required = true)
    @NotBlank(message = "服务上级不能为空")
    private String rAccount;

    @ApiModelProperty(value = "市场位置", required = true)
    @NotBlank(message = "请选择市场位置")
    @Max(value = 1)
    @Min(value = 0)
    private int node;
}
