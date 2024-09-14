package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lottery_item", autoResultMap = true)
@ApiModel(value = "LotteryItem对象", description = "抽奖奖品概率")
public class LotteryItem extends BaseModel {


    @ApiModelProperty("活动id")
    @TableField("lottery_id")
    private Long lotteryId;

    @ApiModelProperty("奖项名称")
    @TableField("item_name")
    private String itemName;

    @ApiModelProperty("奖项等级")
    @TableField("level")
    private Integer level;

    @ApiModelProperty("奖项概率")
    @TableField("percent")
    private BigDecimal percent;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("奖品id")
    @TableField("prize_id")
    private Long prizeId;

    @ApiModelProperty("默认奖项")
    @TableField("default_item")
    private Integer defaultItem;

    @ApiModelProperty("权重")
    @TableField("weight")
    private Integer weight;

    @ApiModelProperty("序号")
    @TableField("number")
    private Integer number;




}
