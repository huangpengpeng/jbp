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
 * 市场等级
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lottery", autoResultMap = true)
@ApiModel(value = "Lottery对象", description = "抽奖活动")
public class Lottery extends BaseModel {


    @ApiModelProperty("活动名称")
    @TableField("topic")
    private String topic;

    @ApiModelProperty("活动状态，1-上线，2-下线")
    @TableField("state")
    private Integer state;

    @ApiModelProperty("活动描述")
    @TableField("link")
    private String link;

    @ApiModelProperty("活动图片")
    @TableField("images")
    private String images;

    @ApiModelProperty("开始时间")
    @TableField("start_time")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @TableField("end_time")
    private Date endTime;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("中奖名单是否开启")
    @TableField("is_winners")
    private Boolean isWinners;

    @ApiModelProperty("个人中奖记录是否开启")
    @TableField("is_self_record")
    private Boolean isSelfRecord;

}
