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
@TableName(value = "eb_activity_score_clearing", autoResultMap = true)
@ApiModel(value = "ActivityScoreClearing对象", description = "积分活动结算")
public class ActivityScoreClearing extends BaseModel {

    @ApiModelProperty("用户id")
    @TableField("uId")
    private Integer uid;

    @ApiModelProperty("状态")
    @TableField("status")
    private String status;

    @ApiModelProperty("活动id")
    @TableField("activity_score_id")
    private Integer activityScoreId;

    @ApiModelProperty("分值")
    @TableField("score")
    private Integer score;

    @ApiModelProperty("年卡")
    @TableField("card_count")
    private String cardCount;


}
