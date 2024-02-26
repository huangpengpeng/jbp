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
@ApiModel(value = "SelfScoreGroupRequest对象", description = "个人业绩分组请求对象")
public class SelfScoreGroupRequest implements Serializable {
    @ApiModelProperty("账户")
    private String account;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("变动方向")
    private String action;
}
