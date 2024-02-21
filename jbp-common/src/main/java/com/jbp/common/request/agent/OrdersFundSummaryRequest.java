package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrdersFundSummaryRequest对象", description = "订单资金汇总请求对象")
public class OrdersFundSummaryRequest {

    @ApiModelProperty("单号")
    private String ordersSn;
}
