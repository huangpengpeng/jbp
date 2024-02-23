package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WalletChangeRequest对象", description = "用户兑换请求对象")
public class WalletChangeRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @NotNull(message = "钱包类型不能为空")
    @ApiModelProperty("钱包类型")
    private Integer walletType;

    @ApiModelProperty("交易密码")
    @NotNull(message = "交易密码不能为空")
    private String pwd;

    @NotNull(message = "资金不能为空")
    @ApiModelProperty("资金")
    private BigDecimal amt;

    @NotBlank(message = "附言不能为空")
    @ApiModelProperty("附言")
    private String postscript;
}
