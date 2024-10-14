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
@TableName(value = "pay_user_account", autoResultMap = true)
@ApiModel(value = "PayUserAccount对象", description = "支付收款用户")
public class PayUserAccount extends BaseModel {

    @ApiModelProperty(value = "支付用户ID")
    private Long payUserId;

    @ApiModelProperty(value = "后台商户ID")
    private Integer merId;

    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    @ApiModelProperty(value = "渠道编码")
    private String channelCode;

    @ApiModelProperty(value = "账户名称")
    private String accountName;

    @ApiModelProperty(value = "账户")
    private String accountNo;

    @ApiModelProperty(value = "总金额")
    private BigDecimal balcurAmt;

    @ApiModelProperty(value = "可用金额")
    private BigDecimal balavalAmt;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "是否虚拟账户【未在三方开户，走线下打款提现】")
    private Boolean ifVirtual;
}
