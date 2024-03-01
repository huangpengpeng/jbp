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
@ApiModel(value = "TeamUserRequest对象", description = "团队用户请求对象")
public class TeamUserEditRequest implements Serializable {
    @ApiModelProperty("id")
    private Integer id;
    @ApiModelProperty("团队名")
    private Integer tid;
}
