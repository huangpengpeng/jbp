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
@ApiModel(value = "WalletTransferRequest对象", description = "用户转账请求对象")
public class WalletTransferRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @NotNull(message = "资金不能为空")
    @ApiModelProperty("资金")
    private BigDecimal amt;

    @NotNull(message = "钱包类型不能为空")
    @ApiModelProperty("钱包类型")
    private Integer type;

    @ApiModelProperty("交易密码")
    @NotNull(message = "交易密码不能为空")
    private String pwd;

    @NotBlank(message = "附言不能为空")
    @ApiModelProperty("附言")
    private String postscript;

    @NotBlank(message = "转账用户账号不能为空")
    @ApiModelProperty("转账用户账号")
    private String account;

    @NotBlank(message = "验证码")
    @ApiModelProperty("验证码")
    private String code;
}
