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
@ApiModel(value = "TeamItemAddRequest对象", description = "团队项目添加对象")
public class TeamItemAddRequest implements Serializable {
    @ApiModelProperty("团队名称")
    private Integer tid;

    @ApiModelProperty("项目名称")
    private String name;

}
