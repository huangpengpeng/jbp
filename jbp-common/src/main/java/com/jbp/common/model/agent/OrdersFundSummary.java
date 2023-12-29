package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.VersionModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 订单资金汇总
 */
@Data
@TableName("OrdersFundSummary")
@NoArgsConstructor
public class OrdersFundSummary extends VersionModel {

    public OrdersFundSummary(Long ordersId, String ordersSn, BigDecimal payPrice, BigDecimal pv) {
        this.ordersId = ordersId;
        this.ordersSn = ordersSn;
        this.payPrice = payPrice;
        this.pv = pv;
        this.commAmt = BigDecimal.ZERO;
    }

    @ApiModelProperty("单号")
    @TableField("ordersId")
    private Long ordersId;

    @ApiModelProperty("单号")
    @TableField("ordersSn")
    private String ordersSn;

    @ApiModelProperty("支付金额")
    @TableField("payPrice")
    private BigDecimal payPrice;

    @ApiModelProperty("总PV")
    @TableField("pv")
    private BigDecimal pv;

    @ApiModelProperty("支出佣金")
    @TableField("commAmt")
    private BigDecimal commAmt;
}
