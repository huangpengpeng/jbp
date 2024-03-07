package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderProductProfitRequest对象", description = "订单收益对象")
public class OrderProductProfitRequest {

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "状态 成功, 退回, 待定")
    private String status;

    @ApiModelProperty(value = "收益名称")
    private String profitName;
}
