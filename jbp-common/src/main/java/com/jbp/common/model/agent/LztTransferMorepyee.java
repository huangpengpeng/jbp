package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.constants.LianLianPayConfig;
import com.jbp.common.lianlian.result.QueryPaymentResult;
import com.jbp.common.lianlian.result.TransferMorepyeeResult;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.QueryPaymentResultHandler;
import com.jbp.common.mybatis.TransferMorepyeeResultHandler;
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
@TableName(value = "eb_lzt_transfer_morepyee", autoResultMap = true)
@ApiModel(value="LztTransferMorepyee对象", description="来账通账户内部转账")
@NoArgsConstructor
public class LztTransferMorepyee extends BaseModel {

    public LztTransferMorepyee(Integer merId, String payerId, String payerName,  String payeeId, String payeeName, String txnSeqno,
                               BigDecimal amt, String postscript, TransferMorepyeeResult orderRet, String accpTxno) {
        this.merId = merId;
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.txnSeqno = txnSeqno;
        this.amt = amt;
        this.postscript = postscript;
        this.orderRet = orderRet;
        this.txnStatus = LianLianPayConfig.TxnStatus.交易处理中.getName();
        this.accpTxno = accpTxno;
        this.createTime= DateTimeUtils.getNow();
    }

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "付款人")
    private String payerId;

    @ApiModelProperty(value = "付款人")
    private String payerName;

    @ApiModelProperty(value = "收款人")
    private String payeeId;

    @ApiModelProperty(value = "收款人")
    private String payeeName;

    @ApiModelProperty(value = "单号")
    private String txnSeqno;

    @ApiModelProperty(value = "ACCP单号")
    private String accpTxno;

    @ApiModelProperty(value = "金额")
    private BigDecimal amt;

    @ApiModelProperty(value = "说明")
    private String postscript;

    @ApiModelProperty(value = "下单结果")
    @TableField(value = "orderRet", typeHandler = TransferMorepyeeResultHandler.class)
    private TransferMorepyeeResult orderRet;

    @ApiModelProperty(value = "查询结果")
    @TableField(value = "queryRet", typeHandler = QueryPaymentResultHandler.class)
    private QueryPaymentResult queryRet;

    @ApiModelProperty(value = "结果")
    private String txnStatus;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "完成时间")
    private Date finishTime;

    @ApiModelProperty(value = "商户名称")
    @TableField(exist = false)
    private String merName;
}
