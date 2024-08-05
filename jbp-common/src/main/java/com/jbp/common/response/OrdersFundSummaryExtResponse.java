package com.jbp.common.response;

import com.jbp.common.model.agent.OrdersFundSummary;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrdersFundSummaryExtResponse对象", description="订单汇总")
public class OrdersFundSummaryExtResponse extends OrdersFundSummary {

    @ApiModelProperty(value = "团队")
    private String name;

    @ApiModelProperty(value = "付款时间")
    private Date payTime;

}
