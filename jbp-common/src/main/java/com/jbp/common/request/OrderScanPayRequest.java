package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderScanPayRequest对象", description = "扫码支付请求对象")
public class OrderScanPayRequest implements Serializable {

    @ApiModelProperty(value = "收款人账号")
    @NotEmpty(message = "收款人账号不能为空")
    private String payerId;

    @ApiModelProperty(value = "支付方法  微信  支付宝  银行卡")
    @NotEmpty(message = "支付方法不能为空")
    private String payMethod;

    @ApiModelProperty(value = "支付金额")
    @NotNull(message = "支付金额不能为空")
    private BigDecimal payPrice;
}
