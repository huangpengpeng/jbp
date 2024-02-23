package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrdersRefundMsgRequest对象", description = "订单退款消息请求对象")
public class OrdersRefundMsgRequest implements Serializable {

    @ApiModelProperty("订单编号")
    private String ordersSn;

    @ApiModelProperty("是否已读")
    private Boolean ifRead;

    @ApiModelProperty("退款单号")
    private String refundSn;
}
