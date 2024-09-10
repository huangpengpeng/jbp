package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 平台端订单分页列表响应对象
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
@ApiModel(value="PlatformOrderPageResponse对象", description="平台端订单分页列表响应对象")
public class PlatformOrderPageResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "平台订单号")
    private String platOrderNo;

    @ApiModelProperty(value = "下单场景（报单，换购）")
    private String platform;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal payPrice;

    @ApiModelProperty(value = "支付方式:weixin,alipay,yue")
    private String payType;

    @ApiModelProperty(value = "订单状态（0：待支付，1：待发货,2：部分发货， 3：待核销，4：待收货,5：已收货,6：已完成，9：已取消）")
    private Integer status;

    @ApiModelProperty(value = "退款状态：0 未退款 1 申请中 2 部分退款 3 已退款")
    private Integer refundStatus;

    @ApiModelProperty(value = "取消状态：0-未取消，1-系统取消，2-用户取消")
    private Integer cancelStatus;

    @ApiModelProperty(value = "用户是否删除")
    private Boolean isUserDel;

    @ApiModelProperty(value = "商户是否删除")
    private Boolean isMerchantDel;

    @ApiModelProperty(value = "订单类型:0-普通订单，1-视频号订单")
    private Integer type;

    @ApiModelProperty(value = "订单等级:0-主订单，1-商户订单，2-商户子订单")
    private Integer level;

    @ApiModelProperty(value = "用户备注")
    private String userRemark;

    @ApiModelProperty(value = "商户备注")
    private String merRemark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "配送方式 1=快递 ，2=门店自提")
    private Integer shippingType;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "账号")
    private String uAccount;

    @ApiModelProperty(value = "用户手机号")
    private String uPhone;

    @ApiModelProperty(value = "付款用户ID")
    private Integer payUid;

    @ApiModelProperty(value = "付款用户昵称")
    private String payNickName;

    @ApiModelProperty(value = "付款账号")
    private String payAccount;

    @ApiModelProperty(value = "付款手机号")
    private String payPhone;

    @ApiModelProperty(value = "用户是否注销")
    private Boolean isLogoff;

    @ApiModelProperty(value = "商户名称")
    private String merName;

    @ApiModelProperty(value = "下单前等级名称")
    private String capaName;

    @ApiModelProperty(value = "成功后等级")
    private String successCapaName;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "订单商品扩展信息")
    private String productExtInfo;

    @ApiModelProperty(value = "退款时间")
    private Date refundTime;

    @ApiModelProperty(value = "供货人昵称")
    private String supplyNickname;

    @ApiModelProperty(value = "供货人账号")
    private String supplyAccount;

    @ApiModelProperty(value = "序列号")
    private String number;
}
