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
@ApiModel(value = "TeamItemPageRequest对象", description = "团队项目分页对象")
public class TeamItemPageRequest implements Serializable {

    @ApiModelProperty("团队名称")
    private Integer tid;

    @ApiModelProperty("项目名称")
    private String name;


}
