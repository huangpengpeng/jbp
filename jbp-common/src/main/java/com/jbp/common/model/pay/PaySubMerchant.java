package com.jbp.common.model.pay;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "pay_sub_merchant", autoResultMap = true)
@ApiModel(value = "PaySubMerchant对象", description = "支付子商户")
public class PaySubMerchant extends BaseModel {

    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    @ApiModelProperty(value = "渠道编码")
    private String channelCode;

    @ApiModelProperty(value = "商户名称")
    private String merchantName;

    @ApiModelProperty(value = "支付子商编")
    private String merchantNo;

    @ApiModelProperty(value = "微信 待开 正常 封控 暂停")
    private String wechatStatus;

    @ApiModelProperty(value = "支付宝 待开 正常 封控 暂停")
    private String aliStatus;

    @ApiModelProperty(value = "快捷 待开 正常 封控 暂停")
    private String quickStatus;

    @ApiModelProperty(value = "微信最后支付成功时间")
    private Date wechatLastSuccessTime;

    @ApiModelProperty(value = "支付宝最后支付成功时间")
    private Date aliLastSuccessTime;

    @ApiModelProperty(value = "快捷最后支付成功时间")
    private Date quickLastSuccessTime;
}
