package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "MessageAddRequest对象", description = "系统消息添加对象")
public class MessageAddRequest implements Serializable {

    @ApiModelProperty("标题")
    @NotBlank(message = "标题不能为空")
    private String title;

    @ApiModelProperty("摘要")
    @NotBlank(message = "摘要不能为空")
    private String digest;

    @ApiModelProperty("跳转地址")
    private String skipUrl;

    @ApiModelProperty("弹窗图片")
    private String windowImage;

    @ApiModelProperty("显示图片")
    private String showImage;

    @ApiModelProperty("内容详情")
    @NotBlank(message = "内容详情不能为空")
    private String content;

    @ApiModelProperty("权益模板")
    @NotNull(message = "权益模板不能为空")
    private Long readLimitTempId;

    @ApiModelProperty("发布时间")
    @NotNull(message = "发布时间不能为空")
    private Date issueTime;

    @ApiModelProperty("排序")
    private Integer number;

    @ApiModelProperty("是否弹窗")
    private Boolean isPop;

    @ApiModelProperty("是否置顶")
    private Boolean isTop;
}
