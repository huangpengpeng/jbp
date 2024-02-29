package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableName;
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

    public LztFundTransfer(Integer merId, String lianLianAcct, String txnSeqno, BigDecimal amt, Date txnTime,
                           String bankAccountNo, String postscript, String accpTxno) {
        this.merId = merId;
        this.lianLianAcct = lianLianAcct;
        this.txnSeqno = txnSeqno;
        this.amt = amt;
        this.txnTime = txnTime;
        this.bankAccountNo = bankAccountNo;
        this.postscript = postscript;
        this.accpTxno = accpTxno;
    }

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "连连账户")
    private String lianLianAcct;

    @ApiModelProperty(value = "划拨单号")
    private String txnSeqno;

    @ApiModelProperty(value = "金额")
    private BigDecimal amt;

    @ApiModelProperty(value = "划拨时间")
    private Date txnTime;

    @ApiModelProperty(value = "银行账户")
    private String bankAccountNo;

    @ApiModelProperty(value = "说明")
    private String postscript;

    @ApiModelProperty(value = "受理单号")
    private String accpTxno;

}
