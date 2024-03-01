package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
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

    public LztAcctApply(Integer merId, String userId, String userType, String userNo, String username, String txnSeqno, String accpTxno,
                        String gatewayUrl) {
        this.merId = merId;
        this.userId = userId;
        this.userType = userType;
        this.userNo = userNo;
        this.username = username;
        this.txnSeqno = txnSeqno;
        this.accpTxno = accpTxno;
        this.gatewayUrl = gatewayUrl;
        this.status = "待开户";
    }

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "连连外部用户[本平台默认生成]")
    private String userId;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "连连账户编号")
    private String userNo;

    @ApiModelProperty(value = "连连账户名称")
    private String username;

    @ApiModelProperty(value = "请求流水号")
    private String txnSeqno;

    @ApiModelProperty(value = "连连请求流水号")
    private String accpTxno;

    @ApiModelProperty(value = "注册URL")
    private String gatewayUrl;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "开户返回消息")
    private String retMsg;

    @ApiModelProperty(value = "商户名称")
    @TableField(exist = false)
    private String merName;
}
