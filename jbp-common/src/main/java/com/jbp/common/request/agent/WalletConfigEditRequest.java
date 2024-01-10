package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
    @ApiModelProperty("可体现")
    private Boolean canWithdraw;
    @ApiModelProperty("可充值")
    private Boolean recharge;
    @ApiModelProperty("兑换目标积分 ")
    @TableField("change_type")
    private int changeType;
    @ApiModelProperty("兑换比例 ")
    @TableField("change_scale")
    private int changeScale;
}
