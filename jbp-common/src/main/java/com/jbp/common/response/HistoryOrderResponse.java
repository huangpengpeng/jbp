package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="HistoryOrderResponse对象", description="历史订单对象")
public class HistoryOrderResponse implements Serializable {

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "用户账户")
    private String account;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付邮费")
    private BigDecimal freightPrice;

    @ApiModelProperty(value = "收货人")
    private String receiveName;

    @ApiModelProperty(value = "收货手机")
    private String receiveMobile;

    @ApiModelProperty(value = "地址信息")
    private String address;

    @ApiModelProperty(value = "物流名称")
    private String shipName;

    @ApiModelProperty(value = "物流单号")
    private String shipSn;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "付款时间")
    private Date payTime;

    @ApiModelProperty(value = "发货时间")
    private Date shipTime;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "订单商品")
    private List<HistoryOrderDetailResponse> goodsDetails;

}
