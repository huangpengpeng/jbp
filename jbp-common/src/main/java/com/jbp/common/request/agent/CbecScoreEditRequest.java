package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CbecScoreEditRequest {

    @ApiModelProperty(value = "渠道")
    @NotBlank(message = "渠道不能为空")
    private String channel;

    @ApiModelProperty(value = "账户")
    @NotBlank(message = "账户不能为空")
    private String account;

    @ApiModelProperty(value = "单号")
    @NotBlank(message = "单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "方向 1 增加  0 减少 ")
    @NotNull(message = "方向不能为空")
    private Integer action;

    @ApiModelProperty(value = "附言")
    @NotBlank(message = "附言不能为空")
    private String postscript;

    @ApiModelProperty(value = "积分金额")
    @NotNull(message = "积分金额不能为空")
    private BigDecimal score;

}
