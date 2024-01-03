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
@ApiModel(value = "TeamEditRequest对象", description = "团队管理更新对象")
public class TeamEditRequest implements Serializable {

    @ApiModelProperty(value = "记录id")
    private Integer id;

    @ApiModelProperty("团队名称")
    private String name;

}
