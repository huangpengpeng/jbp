package com.jbp.common.model.pay;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "pay_user_sub_merchant", autoResultMap = true)
@ApiModel(value = "PayUserSubMerchant对象", description = "支付收款用户子商编绑定")
public class PayUserSubMerchant extends BaseModel {

    @ApiModelProperty(value = "支付用户ID")
    private Long payUserId;

    @ApiModelProperty(value = "收款账户名称")
    private String payUserAccountName;

    @ApiModelProperty(value = "收款账户编号")
    private String payUserAccountNo;

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

    @ApiModelProperty(value = "手续费比例")
    private BigDecimal commScale;
}
