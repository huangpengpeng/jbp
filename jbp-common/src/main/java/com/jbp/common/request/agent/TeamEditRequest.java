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
@ApiModel(value = "TeamEditRequest对象", description = "团队管理更新对象")
public class TeamEditRequest implements Serializable {

    @NotNull(message = "团队管理编号不能为空")
    @ApiModelProperty(value = "记录id")
    private Integer id;

    @NotBlank(message = "团队名称不能为空")
    @ApiModelProperty("团队名称")
    private String name;

}
