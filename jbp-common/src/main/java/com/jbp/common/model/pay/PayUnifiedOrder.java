package com.jbp.common.model.pay;

import com.alipay.api.domain.OrderInfoDTO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.dto.PayOrderInfoDto;
import com.jbp.common.model.VersionModel;
import com.jbp.common.mybatis.FundClearingItemListHandler;
import com.jbp.common.mybatis.PayOrderInfoListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "pay_unified_order", autoResultMap = true)
@ApiModel(value = "PayUnifiedOrder对象", description = "支付通用收款单")
public class PayUnifiedOrder extends VersionModel {

    @ApiModelProperty(value = "后台商户ID")
    private Integer merId;

    @ApiModelProperty(value = "用户编号")
    private String userNo;

    @ApiModelProperty(value = "支付用户ID")
    private Long payUserId;

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

    @ApiModelProperty(value = "业务单号")
    private String txnSeqno;

    @ApiModelProperty(value = "三方渠道单号")
    private String payChannelSeqno;

    @ApiModelProperty(value = "账单单号")
    private String billNo;

    @ApiModelProperty(value = "订单信息透传字段")
    @TableField(value = "orderInfo", typeHandler = PayOrderInfoListHandler.class)
    private List<PayOrderInfoDto> orderInfo;

    @ApiModelProperty(value = "透传扩展字段")
    private String ext;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payAmt;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmt;

    @ApiModelProperty(value = "支付状态  SUCCESS CLOSE FAIL PROCESSING ")
    private String status;

    @ApiModelProperty(value = "付款时间")
    private Date payTime;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "通知地址")
    private String notifyUrl;

    @ApiModelProperty(value = "跳转地址")
    private String returnUrl;

    @ApiModelProperty(value = "客户端ip")
    private String ip;
}
