package com.jbp.common.model.pay;

import com.alipay.api.domain.OrderInfoDTO;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.PayOrderInfoListHandler;
import com.jbp.common.utils.CrmebUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "Pay_cash", autoResultMap = true)
@ApiModel(value = "PayCash对象", description = "支付收银台")
public class PayCash extends BaseModel {

    @ApiModelProperty(value = "收银台token")
    private String token;

    @ApiModelProperty(value = "应用key")
    private String appKey;

    @ApiModelProperty(value = "业务单号")
    private String txnSeqno;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal payAmt;

    @ApiModelProperty(value = "订单信息透传字段")
    @TableField(value = "orderInfo", typeHandler = PayOrderInfoListHandler.class)
    private List<OrderInfoDTO> orderInfo;

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
}
