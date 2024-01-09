package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WalletConfigRequest对象", description = "积分配置请求对象")
public class WalletConfigRequest  implements Serializable {
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("状态")
    private Integer status;
    @ApiModelProperty("可体现")
    private Boolean canWithdraw;
    @ApiModelProperty("可充值")
    private Boolean recharge;
}
