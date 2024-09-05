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
@ApiModel(value = "UserScoreEditRequest对象", description = "修改分数请求对象")
public class UserScoreEditRequest implements Serializable {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("分数")
    private Integer score;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("备注")
    private String remark;
}
