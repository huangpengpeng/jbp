package com.jbp.common.model.pay;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
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
@TableName(value = "pay_user_withdrawal", autoResultMap = true)
@ApiModel(value = "PayUserWithdrawal对象", description = "支付收款账户提现")
public class PayUserWithdrawal extends BaseModel {

    @ApiModelProperty(value = "支付用户ID")
    private Long payUserId;

    @ApiModelProperty(value = "后台商户ID")
    private Integer merId;

    @ApiModelProperty(value = "渠道名称")
    private String channelName;

    @ApiModelProperty(value = "渠道编码")
    private String channelCode;

    @ApiModelProperty(value = "账户名称")
    private String payUserAccountName;

    @ApiModelProperty(value = "账户")
    private String payUserAccountNo;

    @ApiModelProperty(value = "提现单号")
    private String withdrawalSeqno;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal amt;

    @ApiModelProperty(value = "提现状态")
    private String status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "完成时间")
    private Date finishTime;
}
