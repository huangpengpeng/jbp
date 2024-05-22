package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 市场等级
 */

@Data
public class CapaXsRequest {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty("等级名字")
    @NotEmpty(message = "等级名字名称不能为空")
    private String name;

    @ApiModelProperty("下一个级别")
    private Long pCapaId;

    @ApiModelProperty("数字等级")
    @NotNull(message = "数字等级不能为空")
    private Integer rankNum;

    @ApiModelProperty("等级图标地址")
    private String iconUrl;

    @ApiModelProperty("升级提醒图片")
    private String riseImgUrl;

    @ApiModelProperty("邀请图片")
    private String shareImgUrl;

    @ApiModelProperty("计算表达式")
    private String parser;


}
