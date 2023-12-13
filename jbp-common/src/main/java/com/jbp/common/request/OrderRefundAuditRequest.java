package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

/**
 * 订单退款审核请求对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2022 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderRefundAuditRequest对象", description = "订单退款审核请求对象")
public class OrderRefundAuditRequest {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "退款订单号", required = true)
    @NotEmpty(message = "退款订单号不能为空")
    private String refundOrderNo;

    @ApiModelProperty(value = "拒绝退款原因，拒绝退款时必传")
    private String reason;
}
