package com.jbp.common.model.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import com.jbp.common.utils.DateTimeUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_order_pay_refund_record", autoResultMap = true)
@ApiModel(value = "OrderPayRefundRecord对象", description = "订单支付退款记录")
public class OrderPayRefundRecord extends BaseModel {

    public OrderPayRefundRecord(Long orderPayChannelId, Long orderPayRecordId, String payRefundNo, BigDecimal refundPrice, String remark) {
        this.orderPayChannelId = orderPayChannelId;
        this.orderPayRecordId = orderPayRecordId;
        this.payRefundNo = payRefundNo;
        this.status = "退款中";
        this.refundPrice = refundPrice;
        this.createTime = DateTimeUtils.getNow();
        this.refundTime = createTime;
        this.remark = remark;
    }

    @ApiModelProperty(value = "支付渠道ID")
    private Long orderPayChannelId;

    @ApiModelProperty(value = "订单支付记录")
    private Long orderPayRecordId;

    @ApiModelProperty(value = "退款单号")
    private String payRefundNo;

    @ApiModelProperty(value = "回执单号")
    private String receiptNo;

    @ApiModelProperty(value = "订单状态（已取消,  退款中  已退款  退款失败）")
    private String status;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundPrice;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "退款时间")
    private Date refundTime;

    @ApiModelProperty(value = "退款备注")
    private String remark;

    @ApiModelProperty(value = "下单结果")
    private String orderResultInfo;

    @ApiModelProperty(value = "查询结果")
    private String queryResultInfo;
}
