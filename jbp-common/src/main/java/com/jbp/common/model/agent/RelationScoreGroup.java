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
@TableName(value = "eb_relation_score_group", autoResultMap = true)
@ApiModel(value="RelationScoreGroup对象", description="服务业绩分组")
public class RelationScoreGroup extends BaseModel {

    public RelationScoreGroup(Integer uid, int node, String groupName,
                              String startTime, String endTime, String action) {
        this.uid = uid;
        this.score = BigDecimal.ZERO;
        this.node = node;
        this.groupName = groupName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.action = action;
    }

    @ApiModelProperty("用户id")
    private Integer uid;

    @ApiModelProperty("变动方向")
    @TableField("action")
    private String action;

    @ApiModelProperty("积分")
    @TableField("score")
    private BigDecimal score;

    @ApiModelProperty("点位")
    @TableField("node")
    private int node;

    @ApiModelProperty("分组名称")
    @TableField("groupName")
    private String groupName;

    @ApiModelProperty("开始时间")
    @TableField("startTime")
    private String startTime;

    @ApiModelProperty("结束时间")
    @TableField("endTime")
    private String endTime;

    @ApiModelProperty("用户账户")
    @TableField(exist = false)
    private String accountNo;
}
