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
@ApiModel(value = "MessagePageRequest对象", description = "系统消息分页对象")
public class MessagePageRequest implements Serializable {

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("是否弹窗")
    private Boolean isPop;

    @ApiModelProperty("是否置顶")
    private Boolean isTop;

    @ApiModelProperty("状态")
    private Boolean status;

}
