package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lottery_user", autoResultMap = true)
@ApiModel(value = "LotteryUser对象", description = "抽奖次数")
public class LotteryUser extends BaseModel {


    @ApiModelProperty("活动用户")
    @TableField("uid")
    private Long uid;

    @ApiModelProperty("抽奖活动")
    @TableField("lottery_id")
    private Long lotteryId;
    @ApiModelProperty("抽奖剩余次数")
    @TableField("number")
    private Integer number;

}
