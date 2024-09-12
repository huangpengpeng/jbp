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
@TableName(value = "eb_lottery_record", autoResultMap = true)
@ApiModel(value = "LotteryRecord对象", description = "中奖记录")
public class LotteryRecord extends BaseModel {


    @ApiModelProperty("中奖ip")
    @TableField("account_ip")
    private String accountIp;

    @ApiModelProperty("中奖商品")
    @TableField("item_id")
    private Long itemId;

    @ApiModelProperty("中奖商品名称")
    @TableField("prize_name")
    private String prizeName;

    @ApiModelProperty("中奖时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("活动用户")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("奖品类型， -1-谢谢参与、1-普通奖品")
    @TableField(exist = false)
    private Integer prizeType;

    @ApiModelProperty("用户账号")
    @TableField(exist = false)
    private String account;

    @ApiModelProperty("用户昵称")
    @TableField(exist = false)
    private String nickname;


}
