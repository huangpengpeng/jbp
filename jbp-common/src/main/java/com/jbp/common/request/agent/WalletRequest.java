package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WalletRequest对象", description = "用户积分请求对象")
public class WalletRequest implements Serializable {
    @ApiModelProperty("用户账号")
    private String account;

    @ApiModelProperty("钱包类型")
    private Integer type;
}
