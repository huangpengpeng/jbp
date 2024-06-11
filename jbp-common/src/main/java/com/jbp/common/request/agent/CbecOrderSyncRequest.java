package com.jbp.common.request.agent;

import com.jbp.common.dto.CbecOrderDetailDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CbecOrderSyncRequest {

    @ApiModelProperty(value = "渠道")
    @NotBlank(message = "渠道不能为空")
    private String channel;

    @ApiModelProperty(value = "账户")
    @NotBlank(message = "账户不能为空")
    private String account;

    @ApiModelProperty(value = "单号")
    @NotBlank(message = "单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty(value = "已发货 已付款 已退款")
    @NotBlank(message = "状态不能为空")
    private String status;

    @ApiModelProperty(value = "支付总价")
    @NotNull(message = "支付总价不能为空")
    private BigDecimal totalFee;

    @ApiModelProperty(value = "支付商品总价")
    @NotNull(message = "支付商品总价不能为空")
    private BigDecimal goodsFee;

    @ApiModelProperty(value = "支付邮费")
    @NotNull(message = "支付邮费不能为空")
    private BigDecimal postFee;

    @ApiModelProperty(value = "支付积分")
    @NotNull(message = "支付积分不能为空")
    private BigDecimal score;

    @ApiModelProperty(value = "业绩PV")
    @NotNull(message = "业绩PV不能为空")
    private BigDecimal pv;

    @ApiModelProperty(value = "创建时间")
    @NotBlank(message = "创建时间不能为空")
    private String createTime;

    @ApiModelProperty(value = "付款时间")
    @NotBlank(message = "付款时间不能为空")
    private String paymentTime;

    @ApiModelProperty(value = "发货时间")
    private String shipmentsTime;

    @ApiModelProperty(value = "退款时间")
    private String refundTime;

    @ApiModelProperty(value = "商品详情")
    @NotEmpty(message = "商品详情详情不能为空")
    private List<CbecOrderDetailDto> goodsDetails;
}
