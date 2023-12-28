package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_self_score_group", autoResultMap = true)
@ApiModel(value="SelfScoreGroup对象", description="个人业绩分组")
public class SelfScoreGroup extends BaseModel {

    public SelfScoreGroup(Integer uid, String groupName, String startTime, String endTime, String action) {
        this.uid = uid;
        this.score = BigDecimal.ZERO;
        this.groupName = groupName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.action = action;
    }

    @ApiModelProperty("用户id")
    private Integer uid;

    @ApiModelProperty("积分")
    @TableField("score")
    private BigDecimal score;

    @ApiModelProperty("变动方向")
    @TableField("action")
    private String action;

    @ApiModelProperty("分组名称")
    @TableField("groupName")
    private String groupName;

    @ApiModelProperty("开始时间")
    @TableField("startTime")
    private String startTime;

    @ApiModelProperty("结束时间")
    @TableField("endTime")
    private String endTime;
}
