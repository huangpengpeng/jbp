package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

import com.jbp.common.vo.PreOrderInfoVo;

/**
 * 支付配置响应对象
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
@ApiModel(value = "PayConfigResponse对象", description = "支付配置响应对象")
public class PayConfigResponse implements Serializable {

    private static final long serialVersionUID = 7282892323898493847L;

    @ApiModelProperty(value = "余额支付")
    private Boolean yuePayStatus;

    @ApiModelProperty(value = "微信支付")
    private Boolean payWechatOpen;

    @ApiModelProperty(value = "支付宝支付")
    private Boolean aliPayStatus;

    @ApiModelProperty(value = "连连支付")
    private Boolean lianLianStatus;

    @ApiModelProperty(value = "快钱支付")
    private Boolean kqPayStatus;

    @ApiModelProperty(value = "积分支付")
    private Boolean walletStatus;

    @ApiModelProperty(value = "积分支付开启密码")
    private Boolean walletPayOpenPassword;

    @ApiModelProperty(value = "用户余额")
    private BigDecimal userBalance;

    @ApiModelProperty(value = "用户积分")
    private BigDecimal walletBalance;

    @ApiModelProperty(value = "易宝支付宝")
    private Boolean yopAliPayStatus;

    @ApiModelProperty(value = "易宝快捷")
    private Boolean yopQuickPay;

    @ApiModelProperty(value = "易宝微信")
    private Boolean yopWechatPay;

    @ApiModelProperty(value = "京东支付")
    private Boolean jdStatus;

    @ApiModelProperty(value = "京东支付宝")
    private Boolean jdAliPayStatus;

    @ApiModelProperty(value = "京东快捷")
    private Boolean jdQuickPay;
}
