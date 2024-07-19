package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_message")
@ApiModel(value = "Message对象", description = "消息对象")
public class Message extends BaseModel{

    @ApiModelProperty("消息类型->1:弹窗公告 2:系统通知")
    @TableField("type")
    private Integer type;

    @ApiModelProperty("标题")
    @TableField("title")
    private String title;

    @ApiModelProperty("摘要")
    @TableField("digest")
    private String digest;

    @ApiModelProperty("跳转地址")
    @TableField("skip_url")
    private String skipUrl;

    @ApiModelProperty("弹窗图片")
    @TableField("window_image")
    private String windowImage;

    @ApiModelProperty("显示图片")
    @TableField("show_image")
    private String showImage;

    @ApiModelProperty("内容详情")
    @TableField("content")
    private String content;

    @ApiModelProperty("权益模板")
    @TableField("read_limit_temp_id")
    private Long readLimitTempId;

    @ApiModelProperty("发布时间")
    @TableField("issue_time")
    private Date issueTime;

    @ApiModelProperty("排序")
    @TableField("number")
    private Integer number;

    @ApiModelProperty("是否弹窗")
    @TableField("is_pop")
    private Boolean isPop;

    @ApiModelProperty("是否置顶")
    @TableField("is_top")
    private Boolean isTop;

    @ApiModelProperty("状态")
    @TableField("status")
    private Boolean status;

    @ApiModelProperty("浏览量")
    @TableField("page_view")
    private Long pageView;

    @ApiModelProperty("是否已读")
    @TableField(exist = false)
    private Boolean isRead;
}
