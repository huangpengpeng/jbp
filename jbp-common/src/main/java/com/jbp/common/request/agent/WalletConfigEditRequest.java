package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WalletConfigEditRequest对象", description = "积分修改请求对象")
public class WalletConfigEditRequest {
    @ApiModelProperty("记录Id")
    private Integer id;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("状态")
    private int status;
    @ApiModelProperty("可抵扣")
    private Boolean canDeduction;
    @ApiModelProperty("可支付商品")
    private Boolean canPay;
    @ApiModelProperty("可提现")
    private Boolean canWithdraw;
    @ApiModelProperty("可充值")
    private Boolean recharge;
    @ApiModelProperty("可转账")
    private Boolean canTransfer;
    @ApiModelProperty("兑换目标积分 ")
    private Integer changeType;
    @ApiModelProperty("兑换比例 ")
    private BigDecimal changeScale;
}
