package com.jbp.common.model.pay;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.VersionModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "pay_unified_refund_order", autoResultMap = true)
@ApiModel(value = "PayUnifiedRefundOrder对象", description = "支付通用退款单")
public class PayUnifiedRefundOrder extends VersionModel {

    @ApiModelProperty(value = "后台商户ID")
    private Integer merId;

    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    @ApiModelProperty(value = "渠道编码")
    private String channelCode;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "支付子商编")
    private String merchantNo;

    @ApiModelProperty(value = "收款用户名称")
    private String payUserAccountName;

    @ApiModelProperty(value = "收款用户账户")
    private String payUserAccountNo;

    @ApiModelProperty(value = "支付方法")
    private String payMethod;

    @ApiModelProperty(value = "支付单号")
    private String paySeqno;

    @ApiModelProperty(value = "业务单号")
    private String txnSeqno;

    @ApiModelProperty(value = "三方渠道单号")
    private String payChannelSeqno;

    @ApiModelProperty(value = "订单信息透传字段")
    private String orderInfo;

    @ApiModelProperty(value = "透传扩展字段")
    private String ext;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payAmt;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmt;

    @ApiModelProperty(value = "支付状态")
    private String status;

    @ApiModelProperty(value = "退款时间")
    private Date refundTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;
}
