package com.jbp.common.request.agent;

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
@ApiModel(value = "WalletformEditRequest对象", description = "用户积分更新对象")
public class WalletformEditRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户账户不能为空")
    @ApiModelProperty("用户账户")
    private String account;

    @NotNull(message = "钱包类型不能为空")
    @ApiModelProperty("钱包类型")
    private Integer type;

    @NotBlank(message = "外部单号不能为空")
    @ApiModelProperty("外部单号")
    private String externalNo;

    @NotBlank(message = "附言不能为空")
    @ApiModelProperty("附言")
    private String postscript;

    @NotNull(message = "资金不能为空")
    @ApiModelProperty("资金")
    private BigDecimal amt;
}
