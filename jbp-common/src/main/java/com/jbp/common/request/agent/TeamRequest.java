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
@ApiModel(value = "TeamRequest对象", description = "团队管理请求对象")
public class TeamRequest implements Serializable {

    @NotBlank(message = "团队名称不能为空")
    @ApiModelProperty("团队名称")
    private String name;

    @NotBlank(message = "团队领导用户账号不能为空")
    @ApiModelProperty("团队领导用户账号")
    private String account;

}
