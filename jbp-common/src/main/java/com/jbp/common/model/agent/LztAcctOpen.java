package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.lianlian.result.AcctInfo;
import com.jbp.common.lianlian.result.LztQueryAcctInfo;
import com.jbp.common.lianlian.result.UserInfoResult;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.UserInfoResultHandler;
import com.jbp.common.mybatis.WithdrawalResultHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lzt_acct_open", autoResultMap = true)
@ApiModel(value="LztAcctOpen对象", description="来账通账户开通")
@NoArgsConstructor
public class LztAcctOpen extends BaseModel {

    public LztAcctOpen(Integer merId, String userId, String txnSeqno, String accpTxno,
                       String userType, String flagChnl, Date txnTime, String gatewayUrl) {
        this.merId = merId;
        this.userId = userId;
        this.txnSeqno = txnSeqno;
        this.accpTxno = accpTxno;
        this.userType = userType;
        this.flagChnl = flagChnl;
        this.txnTime = txnTime;
        this.gatewayUrl = gatewayUrl;
        this.status = "待开户";
    }

    @ApiModelProperty(value = "商户ID")
    private Integer merId;

    @ApiModelProperty(value = "连连外部用户[本平台默认生成]")
    private String userId;

    @ApiModelProperty(value = "请求流水号")
    private String txnSeqno;

    @ApiModelProperty(value = "连连请求流水号")
    private String accpTxno;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "申请渠道")
    private String flagChnl;

    @ApiModelProperty(value = "交易时间")
    private Date txnTime;

    @ApiModelProperty(value = "开户地址")
    private String gatewayUrl;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "开户返回消息")
    private String retMsg;

    @ApiModelProperty(value = "开户返回消息")
    @TableField(value = "queryRet", typeHandler = UserInfoResultHandler.class)
    private UserInfoResult queryRet;

    @ApiModelProperty(value = "通知信息")
    private String notifyInfo;

    @ApiModelProperty(value = "商户名称")
    @TableField(exist = false)
    private String merName;
}
