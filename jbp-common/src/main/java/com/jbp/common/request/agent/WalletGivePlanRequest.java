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
@ApiModel(value = "WalletGivePlanRequest", description = "用户钱包奖励计划请求对象")
public class WalletGivePlanRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户id")
    private Integer uid;

    @ApiModelProperty("账户")
    private String account;

    @ApiModelProperty("钱包类型")
    private Integer walletType;

    @ApiModelProperty("外部单号")
    private String externalNo;

}
