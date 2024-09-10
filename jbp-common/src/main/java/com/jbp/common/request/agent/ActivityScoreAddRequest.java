package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ActivityScoreAddRequest对象", description = "积分活动添加对象")
public class ActivityScoreAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "活动名称不能为空")
    @ApiModelProperty("活动名称")
    private String name;

    @NotNull(message = "活动开始时间不能为空")
    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @NotNull(message = "活动结束时间不能为空")
    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @NotNull(message = "活动等级要求不能为空！")
    @ApiModelProperty("活动等级要求")
    private Integer capaId;

    @NotBlank(message = "状态不能为空")
    @ApiModelProperty("状态（开启，关闭）")
    private String status;

    @NotBlank(message = "状态不能为空")
    @ApiModelProperty("分数规则设置json")
    private String rule;

    @NotBlank(message = "活动描述不能为空")
    @ApiModelProperty("活动描述")
    private String mark;
}
