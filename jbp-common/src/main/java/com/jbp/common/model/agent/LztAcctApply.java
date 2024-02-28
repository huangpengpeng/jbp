package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lzt_acct_apply", autoResultMap = true)
@ApiModel(value="LztAcctApply对象", description="来账通账户申请")
@NoArgsConstructor
public class LztAcctApply extends BaseModel {

    public LztAcctApply(Integer merId, String lianLianAcct, String txnSeqno, String accpTxno,
                        String gatewayUrl) {
        this.merId = merId;
        this.lianLianAcct = lianLianAcct;
        this.txnSeqno = txnSeqno;
        this.accpTxno = accpTxno;
        this.gatewayUrl = gatewayUrl;
        this.status = "待开户";
    }

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "连连账户")
    private String lianLianAcct;

    @ApiModelProperty(value = "请求流水号")
    private String txnSeqno;

    @ApiModelProperty(value = "连连请求流水号")
    private String accpTxno;

    @ApiModelProperty(value = "注册URL")
    private String gatewayUrl;

    @ApiModelProperty(value = "状态   待开户  已完成")
    private String status;

    @ApiModelProperty(value = "开户返回消息")
    private String retMsg;
}
