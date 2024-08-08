package com.jbp.common.vo;

import com.jbp.common.model.order.OrderExt;
import com.jbp.common.model.product.ProductDeduction;
import com.jbp.common.request.RegisterOrderRequest;
import com.jbp.common.request.RiseOrderRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * 预下单Vo对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PreOrderInfoVo对象", description = "预下单Vo对象")
public class PreOrderInfoVo {

    @ApiModelProperty(value = "下单用户ID")
    private Integer uid;

    @ApiModelProperty(value = "付款用户ID")
    private Integer payUserId;

    @ApiModelProperty(value = "商品总计金额")
    private BigDecimal proTotalFee;

    @ApiModelProperty(value = "订单商品数量")
    private Integer orderProNum;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal freightFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal couponFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "商户优惠金额")
    private BigDecimal merCouponFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "平台优惠金额")
    private BigDecimal platCouponFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "钱包积分抵扣金额")
    private BigDecimal walletDeductionFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payFee;

    @ApiModelProperty(value = "支付方式: -1 统一支付 0 在线支付 1 积分支付")
    private Integer payGateway;

    @ApiModelProperty(value = "平台优惠券编号（选择优惠券时有值,不选时为0")
    private Integer platUserCouponId = 0;

    @ApiModelProperty(value = "地址id")
    private Integer addressId;

    @ApiModelProperty(value = "用户剩余积分")
    private Integer userIntegral;

    @ApiModelProperty(value = "用户可用余额")
    private BigDecimal userBalance;

    @ApiModelProperty(value = "商户订单数组")
    private List<PreMerchantOrderVo> merchantOrderVoList;

    @ApiModelProperty(value = "购物车编号列表")
    private List<Integer> cartIdList;

    @ApiModelProperty(value = "积分抵扣开关")
    private Boolean integralDeductionSwitch;

    @ApiModelProperty(value = "用户是否使用积分抵扣")
    private Boolean isUseIntegral;

    @ApiModelProperty(value = "订单类型:0-普通订单，1-视频号订单,2-秒杀订单 ,3-发货单,4-定货")
    private Integer type = 0;

    @ApiModelProperty(value = "钱包积分抵扣")
    private List<ProductDeduction> wallwtDeductionList;


    @ApiModelProperty(value = "注册用户信息")
    private RegisterOrderRequest registerInfo;

    @ApiModelProperty(value = "晋升用户信息")
    private RiseOrderRequest riseInfo;

    @ApiModelProperty(value = "订单扩展信息")
    private OrderExt orderExt;

    @ApiModelProperty(value = "运费模板")
    private String freightName;
}
