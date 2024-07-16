package com.jbp.common.model.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_order_pay_channel", autoResultMap = true)
@ApiModel(value = "OrderPayChannel对象", description = "支付渠道")
public class OrderPayChannel implements Serializable {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "支付渠道 lianlian, yop, kq")
    private String payChannel;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "父级商户号")
    private String parentMerchantNo;

    @ApiModelProperty(value = "支付商户号")
    private String merchantNo;

    @ApiModelProperty(value = "支付方法")
    private String payMethod;

    @ApiModelProperty(value = "公钥")
    private String pubKey;

    @ApiModelProperty(value = "私钥")
    private String priKey;

    @ApiModelProperty(value = "通知地址")
    private String notifyUrl;

    @ApiModelProperty(value = "跳转地址")
    private String returnUrl;

    @ApiModelProperty(value = "其他支付信息")
    private String otherInfo;

    @ApiModelProperty(value = "权重")
    private int weight;
}
