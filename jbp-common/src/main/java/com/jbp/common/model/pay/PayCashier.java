package com.jbp.common.model.pay;

import com.alipay.api.domain.OrderInfoDTO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.dto.PayOrderInfoDto;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.PayOrderInfoListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "pay_cashier", autoResultMap = true)
@ApiModel(value = "PayCashier对象", description = "支付收银台")
public class PayCashier extends BaseModel {

    @ApiModelProperty(value = "收银台token")
    private String token;

    @ApiModelProperty(value = "用户编号")
    private String userNo;

    @ApiModelProperty(value = "应用key")
    private String appKey;

    @ApiModelProperty(value = "业务单号")
    private String txnSeqno;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payAmt;

    @ApiModelProperty(value = "订单信息透传字段")
    @TableField(value = "orderInfo", typeHandler = PayOrderInfoListHandler.class)
    private List<PayOrderInfoDto> orderInfo;

    @ApiModelProperty(value = "透传扩展字段")
    private String ext;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
    private Date expireTime;

    @ApiModelProperty(value = "通知地址")
    private String notifyUrl;

    @ApiModelProperty(value = "跳转地址")
    private String returnUrl;

    @ApiModelProperty(value = "客户端ip")
    private String ip;
}
