package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "HistoryOrderShipRequest对象", description = "历史订单请求对象")
public class HistoryOrderShipRequest implements Serializable {


    @ApiModelProperty(value = "下拉选 查询历史项目  水母: wkp42271043176625, 同富：tf138940740527575, 雪康: xcsmall, 易康: jymall")
    private String dbName;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "快递单号")
    private String shipNo;

    @ApiModelProperty(value = "快递公司")
    private String shopNme;
}
