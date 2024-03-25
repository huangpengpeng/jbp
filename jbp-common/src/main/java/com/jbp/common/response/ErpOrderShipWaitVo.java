package com.jbp.common.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class ErpOrderShipWaitVo implements Serializable {

    public ErpOrderShipWaitVo(String userId, String accountNo, String groupName, String username, String mobile, String ordersSn,
                              String payTime, BigDecimal postFee, BigDecimal totalPrice, BigDecimal couponPrice,
                              BigDecimal consumePrice, List<ErpOrderGoodVo> orderGoodVoList, String receiverUsername,
                              String receiverMobile, String province, String city, String area, String address) {
        this.userId = userId;
        this.accountNo = accountNo;
        this.groupName = groupName;
        this.username = username;
        this.mobile = mobile;
        this.ordersSn = ordersSn;
        this.payTime = payTime;
        this.postFee = postFee;
        this.totalPrice = totalPrice;
        this.couponPrice = couponPrice;
        this.consumePrice = consumePrice;
        this.orderGoodVoList = orderGoodVoList;
        this.receiverUsername = receiverUsername;
        this.receiverMobile = receiverMobile;
        this.province = province;
        this.city = city;
        this.area = area;
        this.address = address;
    }

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户账户")
    private String accountNo;

    @ApiModelProperty(value = "团队名称")
    private String groupName;

    @ApiModelProperty(value = "用户名称")
    private String username;

    @ApiModelProperty(value = "用户手机号")
    private String mobile;

    @ApiModelProperty(value = "单号")
    private String ordersSn;

    @ApiModelProperty(value = "付款时间")
    private String payTime;

    @ApiModelProperty(value = "运费")
    private BigDecimal postFee;

    @ApiModelProperty(value = "总价")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal couponPrice;

    @ApiModelProperty(value = "消费积分")
    private BigDecimal consumePrice;

    @ApiModelProperty(value = "订单商品")
    private List<ErpOrderGoodVo> orderGoodVoList;

    @ApiModelProperty(value = "收货人")
    private String receiverUsername;

    @ApiModelProperty(value = "收货手机")
    private String receiverMobile;

    @ApiModelProperty(value = "手机号")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String area;

    @ApiModelProperty(value = "详细地址")
    private String address;
}
