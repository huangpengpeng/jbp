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
@ApiModel(value = "OrdersFundSummaryRequest对象", description = "订单资金汇总请求对象")
public class OrdersFundSummaryRequest implements Serializable {

    @ApiModelProperty("单号")
    private String ordersSn;

    @ApiModelProperty("团队id")
    private String teamId;

    @ApiModelProperty("付款开始时间")
    private String startPayTime;

    @ApiModelProperty("付款结束时间")
    private String endPayTime;
}
