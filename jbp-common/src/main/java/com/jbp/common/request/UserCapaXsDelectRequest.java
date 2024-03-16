package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserCapaXsDelectRequest", description = "用户星级删除请求对象")
public class UserCapaXsDelectRequest {

    @NotNull(message = "用户编号不能空")
    @ApiModelProperty("用户编号")
    private Integer uid;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("系统描述")
    private String description;
}
