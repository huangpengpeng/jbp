package com.jbp.common.response;

import com.jbp.common.model.order.OrderInvoiceDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderSalesVolumeResponse对象", description = "订单日销售额对象")
public class OrderSalesVolumeResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "天数")
    private String day;

    @ApiModelProperty(value = "销售总价")
    private String total;
    @ApiModelProperty(value = "退款总价")
    private String refundTotal;

    @ApiModelProperty(value = "退款总价")
    private String ansTotal;

    @ApiModelProperty(value = "用户数")
    private String userTotal;
}
