package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WalletChangeRequest对象", description = "用户兑换请求对象")
public class WalletChangeRequest {
    @NotNull(message = "资金不能为空")
    @ApiModelProperty("资金")
    private BigDecimal amt;
    @NotNull(message = "钱包类型不能为空")
    @ApiModelProperty("钱包类型")
    private Integer type;
    @NotNull(message = "钱包兑换类型不能为空")
    @ApiModelProperty("钱包类型")
    private Integer changeType;
    @ApiModelProperty("交易密码")
    @NotNull(message = "交易密码不能为空")
    private String tradePassword;
    @NotBlank(message = "外部单号不能为空")
    @ApiModelProperty("外部单号")
    private String externalNo;
    @NotBlank(message = "附言不能为空")
    @ApiModelProperty("附言")
    private String postscript;
}