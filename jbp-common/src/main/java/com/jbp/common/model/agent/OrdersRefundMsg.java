package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 订单退款消息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_orders_refund_msg", autoResultMap = true)
@ApiModel(value="OrdersRefundMsg对象", description="订单退款消息")
@NoArgsConstructor
public class OrdersRefundMsg extends BaseModel {

    public OrdersRefundMsg(String ordersSn, String refundSn, String context) {
        this.ordersSn = ordersSn;
        this.refundSn = refundSn;
        this.context = context;
        this.ifRead = false;
    }

    @ApiModelProperty("订单编号")
    @TableField("orders_sn")
    private String ordersSn;

    @ApiModelProperty("退款单号")
    @TableField("refund_sn")
    private String refundSn;

    @ApiModelProperty("事件内容")
    @TableField("context")
    private String context;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("是否已读")
    @TableField("if_read")
    private Boolean ifRead;
}

