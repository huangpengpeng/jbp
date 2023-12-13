package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.jbp.common.model.coupon.Coupon;
import com.jbp.common.model.coupon.CouponUser;
import com.jbp.common.vo.PreOrderInfoDetailVo;

/**
 * 预下单商户部分响应对象
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
@ApiModel(value = "PreOrderMerchantInfoResponse对象", description = "预下单商户部分响应对象")
public class PreOrderMerchantInfoResponse implements Serializable {

    private static final long serialVersionUID = 7282892323898493847L;

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "商品总数")
    private Integer proTotalNum;

    @ApiModelProperty(value = "商品总计金额")
    private BigDecimal proTotalFee;

    @ApiModelProperty(value = "运费金额")
    private BigDecimal freightFee;

    @ApiModelProperty(value = "优惠券编号（选择优惠券时有值,不选时为0")
    private Integer userCouponId = 0;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal couponFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "自提开关:0-关闭，1-开启")
    private Boolean takeTheirSwitch;

    @ApiModelProperty(value = "快递类型: 1-快递配送，2-到店自提")
    private Integer shippingType = 1;

    @ApiModelProperty(value = "收货人姓名")
    private String realName;

    @ApiModelProperty(value = "收货人电话")
    private String userPhone;

    @ApiModelProperty(value = "收货详细地址")
    private String userAddress;

    @ApiModelProperty(value = "使用积分")
    private Integer useIntegral = 0;

    @ApiModelProperty(value = "积分抵扣金额")
    private BigDecimal integralPrice = BigDecimal.ZERO;

    @ApiModelProperty(value = "商户订单详情数组")
    private List<PreOrderInfoDetailVo> orderInfoList;

    @ApiModelProperty(value = "商户优惠金额")
    private BigDecimal merCouponFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "订单商户优惠券列表")
    private List<CouponUser> merCouponUserList;

    @ApiModelProperty(value = "是否自营：0-自营，1-非自营")
    private Boolean isSelf;
}
