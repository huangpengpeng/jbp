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
@ApiModel(value = "HistoryOrderEditRequest对象", description = "历史订单请求对象")
public class HistoryOrderEditRequest implements Serializable {

    @ApiModelProperty(value = "操作类型  0 批量发货  1 批量退款")
    private Integer type;

    @ApiModelProperty(value = "订单列表")
    private List<String> orderNoList;

    @ApiModelProperty(value = "下拉选 查询历史项目  水母: wkp42271043176625, 同富：tf138940740527575, 雪康: xcsmall, 易康: jymall")
    private String dbName;

    @ApiModelProperty(value = "聚水潭店铺ID")
    private String shopId;
}
