package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_wallet_config")
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

    @ApiModelProperty("可抵扣")
    @TableField("can_deduction")
    private Boolean canDeduction;

    @ApiModelProperty("可支付商品")
    @TableField("can_pay")
    private Boolean canPay;

    @ApiModelProperty("可提现")
    @TableField("can_withdraw")
    private Boolean canWithdraw;

    @ApiModelProperty("可充值")
    @TableField("recharge")
    private Boolean recharge;

    @ApiModelProperty("可转账")
    @TableField("can_transfer")
    private Boolean canTransfer;

    @ApiModelProperty("兑换目标积分 ")
    @TableField(value = "change_type", updateStrategy = FieldStrategy.IGNORED)
    private Integer changeType;

    @ApiModelProperty("兑换比例 ")
    @TableField(value = "change_scale", updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal changeScale;
}
