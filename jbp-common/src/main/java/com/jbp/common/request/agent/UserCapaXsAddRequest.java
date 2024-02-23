package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserCapaXsAddRequest对象", description = "用户星级添加请求对象")
public class UserCapaXsAddRequest implements Serializable {

    @NotBlank(message = "用户账号为空")
    @ApiModelProperty("用户账号")
    private String account;

    @NotNull(message = "用户星级不能为空")
    @ApiModelProperty("星级ID")
    private Long capaId;

    @ApiModelProperty("虚拟星级")
    private Boolean ifFake;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("系统描述")
    private String description;
}
