package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrdersRefundMsgReadRequest对象", description = "订单退款消息批量已读")
public class OrdersRefundMsgReadRequest {

    @ApiModelProperty(value = "编号")
    private List<Long> ids;

    @ApiModelProperty("备注")
    private String remark;
}
