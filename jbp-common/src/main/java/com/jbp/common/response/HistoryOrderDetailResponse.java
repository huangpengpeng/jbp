package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="HistoryOrderDetailResponse对象", description="历史订单详情对象")
public class HistoryOrderDetailResponse implements Serializable {

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "商品编号")
    private String goodsSn;

    @ApiModelProperty(value = "商品数量")
    private Integer number;

    @ApiModelProperty(value = "商品图片")
    private String picUrl;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

}
