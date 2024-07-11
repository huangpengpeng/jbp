package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.result.LztTransferResult;
import com.jbp.common.lianlian.result.QueryWithdrawalResult;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.LztTransferResultHandler;
import com.jbp.common.mybatis.QueryWithdrawalResultHandler;
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
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lzt_salary_transfer",autoResultMap = true)
@ApiModel(value="LztSalaryTransfer对象", description="来账通薪资代发")
public class LztSalaryTransfer extends BaseModel {

    public LztSalaryTransfer(Integer merId, String payerId, String payerName, String txnSeqno,
                       BigDecimal amt, BigDecimal feeAmount, String payeeType, String bankAcctNo,
                       String bankName, String bankCode, String bankAcctName,
                             String postscript, String payChannelType, String time) {
        this.merId = merId;
        this.payerId = payerId;
        this.payerName = payerName;
        this.txnSeqno = txnSeqno;
        this.amt = amt;
        this.feeAmount = feeAmount;
        this.payeeType = payeeType;
        this.bankAcctNo = bankAcctNo;
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.bankAcctName = bankAcctName;
        this.postscript = postscript;
        this.receiptStatus = 0;
        this.txnStatus = LianLianPayConfig.TxnStatus.已创建.getName();
        this.createTime = DateTimeUtils.getNow();
        this.payChannelType = payChannelType;
        this.time = time;
    }

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "付款人")
    private String payerId;

    @ApiModelProperty(value = "付款人")
    private String payerName;

    @ApiModelProperty(value = "单号")
    private String txnSeqno;

    @ApiModelProperty(value = "ACCP单号")
    private String accpTxno;

    @ApiModelProperty(value = "金额")
    private BigDecimal amt;

    @ApiModelProperty(value = "手续费")
    private BigDecimal feeAmount;

    /**
     *  对私银行账户：BANKACCT_PRI
     *  对公银行账户：BANKACCT_PUB
     */
    @ApiModelProperty(value = "收款方类型")
    private String payeeType;

    @ApiModelProperty(value = "银行账号")
    private String bankAcctNo;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "银行编码")
    private String bankCode;

    @ApiModelProperty(value = "户名")
    private String bankAcctName;

    @ApiModelProperty(value = "大额行号")
    private String cnapsCode;

    @ApiModelProperty(value = "附言")
    private String postscript;

    @ApiModelProperty(value = "下单结果")
    @TableField(value = "orderRet", typeHandler = LztTransferResultHandler.class)
    private LztTransferResult orderRet;

    @ApiModelProperty(value = "查询结果")
    @TableField(value = "queryRet", typeHandler = QueryWithdrawalResultHandler.class)
    private QueryWithdrawalResult queryRet;

    @ApiModelProperty(value = "回执单状态 0  待申请  1 待下载")
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

    @ApiModelProperty(value = "月份")
    private String time;

    @ApiModelProperty(value = "商户名称")
    @TableField(exist = false)
    private String merName;

    @ApiModelProperty(value = "返回消息")
    @TableField(exist = false)
    private String regMsg;

    @ApiModelProperty(value = "总金额")
    @TableField(exist = false)
    private BigDecimal totalFee;
}
