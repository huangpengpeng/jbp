package com.jbp.common.model.agent;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.result.QueryWithdrawalResult;
import com.jbp.common.lianlian.result.WithdrawalResult;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.QueryWithdrawalResultHandler;
import com.jbp.common.mybatis.WithdrawalResultHandler;
import com.jbp.common.utils.DateTimeUtils;
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
@TableName(value = "eb_lzt_withdrawal", autoResultMap = true)
@ApiModel(value="LztWithdrawal对象", description="来账通提现")
@NoArgsConstructor
public class LztWithdrawal extends BaseModel {

    public LztWithdrawal(Integer merId, String userId, String username, String txnSeqno,
                         String accpTxno, BigDecimal amt, BigDecimal feeAmount, String postscript,
                         WithdrawalResult orderRet, String payChannelType) {
        this.merId = merId;
        this.userId = userId;
        this.username = username;
        this.txnSeqno = txnSeqno;
        this.accpTxno = accpTxno;
        this.amt = amt;
        this.feeAmount = feeAmount;
        this.postscript = postscript;
        this.orderRet = orderRet;
        this.txnStatus = LianLianPayConfig.TxnStatus.交易处理中.getName();
        this.createTime = DateTimeUtils.getNow();
        this.receiptStatus = 0;
        this.payChannelType = payChannelType;
    }

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "提现人")
    private String userId;

    @ApiModelProperty(value = "提现人")
    private String username;

    @ApiModelProperty(value = "提现单号")
    private String txnSeqno;

    @ApiModelProperty(value = "ACCP单号")
    private String accpTxno;

    @ApiModelProperty(value = "提现金额")
    private BigDecimal amt;

    @ApiModelProperty(value = "手续费")
    private BigDecimal feeAmount;

    @ApiModelProperty(value = "说明")
    private String postscript;

    @ApiModelProperty(value = "提现下单返回信息")
    @TableField(value = "orderRet", typeHandler = WithdrawalResultHandler.class)
    private WithdrawalResult orderRet;

    @ApiModelProperty(value = "提现查询返回信息")
    @TableField(value = "queryRet", typeHandler = QueryWithdrawalResultHandler.class)
    private QueryWithdrawalResult queryRet;

    @ApiModelProperty(value = "回执单状态 0  申请回执  1 下载回执")
    private Integer receiptStatus;

    @ApiModelProperty(value = "回执token")
    private String receiptToken;

    @ApiModelProperty(value = "电子回单流水号")
    private String receiptAccpTxno;

    @ApiModelProperty(value = "回执文件")
    private String receiptZip;

    @ApiModelProperty(value = "结果")
    private String txnStatus;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "完成时间")
    private Date finishTime;

    @ApiModelProperty(value = "支付渠道名称")
    private String payChannelType;

    @ApiModelProperty(value = "商户名称")
    @TableField(exist = false)
    private String merName;

    @ApiModelProperty(value = "返回消息")
    @TableField(exist = false)
    private String regMsg;
}
