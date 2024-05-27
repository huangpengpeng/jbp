package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lzt_fund_transfer", autoResultMap = true)
@ApiModel(value="LztFundTransfer对象", description="来账通账户划拨")
@NoArgsConstructor
public class LztFundTransfer extends BaseModel {

    public LztFundTransfer(Integer merId, String userId, String username, String txnSeqno, BigDecimal amt, Date txnTime,
                           String bankAccountNo, String postscript, String accpTxno, String payChannelType) {
        this.merId = merId;
        this.userId = userId;
        this.username = username;
        this.txnSeqno = txnSeqno;
        this.amt = amt;
        this.txnTime = txnTime;
        this.bankAccountNo = bankAccountNo;
        this.postscript = postscript;
        this.accpTxno = accpTxno;
        this.status = LianLianPayConfig.FundTransferStatus.处理中.name();
        this.payChannelType = payChannelType;
    }

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "连连账户")
    private String userId;

    @ApiModelProperty(value = "连连账户")
    private String username;

    @ApiModelProperty(value = "划拨单号")
    private String txnSeqno;

    @ApiModelProperty(value = "金额")
    private BigDecimal amt;

    @ApiModelProperty(value = "手续费")
    private BigDecimal feeAmount;

    @ApiModelProperty(value = "划拨时间")
    private Date txnTime;

    @ApiModelProperty(value = "银行账户")
    private String bankAccountNo;

    @ApiModelProperty(value = "说明")
    private String postscript;

    @ApiModelProperty(value = "受理单号")
    private String accpTxno;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "返回消息")
    private String retMsg;

    @ApiModelProperty(value = "支付渠道名称")
    private String payChannelType;

    @ApiModelProperty(value = "商户名称")
    @TableField(exist = false)
    private String merName;

}
