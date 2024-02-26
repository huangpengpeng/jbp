package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WalletWithdrawPageRequest对象", description = "钱包提现请求对象")
public class WalletWithdrawPageRequest {

    @ApiModelProperty("账户")
    private String account;

    @ApiModelProperty("钱包名称")
    private String walletName;

    @ApiModelProperty("状态")
    private String status;
}
