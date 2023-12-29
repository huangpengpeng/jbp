package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName(value = "OrdersRefundMsg")
@NoArgsConstructor
public class OrdersRefundMsg extends BaseModel {

    public OrdersRefundMsg(String ordersSn, String refundSn, String context) {
        this.ordersSn = ordersSn;
        this.refundSn = refundSn;
        this.context = context;
        this.ifRead = false;
    }

    @ApiModelProperty("订单编号")
    @TableField("ordersSn")
    private String ordersSn;

    @ApiModelProperty("退款单号")
    @TableField("refundSn")
    private String refundSn;

    @ApiModelProperty("事件内容")
    @TableField("context")
    private String context;

    @ApiModelProperty("是否已读")
    @TableField("ifRead")
    private Boolean ifRead;
}

