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
@ApiModel(value = "TeamItemUpdateRequest对象", description = "团队项目修改请求对象")
public class TeamItemUpdateRequest implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("团队名称")
    private Integer tid;

    @ApiModelProperty("项目名称")
    private String name;


}
