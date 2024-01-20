package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wallet_config")
@ApiModel(value = "WalletConfig对象", description = "积分配置信息")
public class WalletConfig {
    @ApiModelProperty(value = "记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty("类型编号")
    @TableField("type")
    private Integer type;
    @ApiModelProperty("名称")
    @TableField("name")
    private String name;
    @ApiModelProperty("状态")
    @TableField("status")
    private int status;

    @ApiModelProperty("可体现 类型： 0禁用 1：启用 ")
    @TableField("can_withdraw")
    private Boolean canWithdraw;
    @ApiModelProperty("可充值 类型： 0禁用 1：启用 ")
    @TableField("recharge")
    // can_deposit
    private Boolean recharge;

    // 转账
    // can_transfer

    @ApiModelProperty("兑换目标积分 ")
    @TableField("change_type")
    private int changeType;
    @ApiModelProperty("兑换比例 ")
    @TableField("change_scale")
    private int changeScale;
}
