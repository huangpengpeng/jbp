package com.jbp.common.model.agent;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 积分活动
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_activity_score", autoResultMap = true)
@ApiModel(value = "ActivityScore对象", description = "积分活动")
public class ActivityScore extends BaseModel {

    @ApiModelProperty("活动名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("活动开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty("活动等级要求")
    @TableField("capa_id")
    private Integer capaId;

    @ApiModelProperty("状态（开启，关闭）")
    @TableField("status")
    private String status;

    @ApiModelProperty("分数规则设置json")
    @TableField("rule")
    private String rule;

    @ApiModelProperty("活动描述")
    @TableField("mark")
    private String mark;

}
