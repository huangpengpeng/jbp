package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.lianlian.result.AcctInfo;
import com.jbp.common.lianlian.result.LztQueryAcctInfo;
import com.jbp.common.lianlian.result.UserInfoResult;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.LztQueryAcctInfoListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lzt_acct", autoResultMap = true)
@ApiModel(value="LztAcct对象", description="来账通账户")
@NoArgsConstructor
public class LztAcct extends BaseModel {

    public LztAcct(Integer merId, String userId, String userType, String userNo, String username, String bankAccount) {
        this.merId = merId;
        this.userId = userId;
        this.userType = userType;
        this.userNo = userNo;
        this.username = username;
        this.bankAccount = bankAccount;
        this.ifOpenBankAcct = false;
    }

    @ApiModelProperty(value = "商户id")
    private Integer merId;

    @ApiModelProperty(value = "连连外部用户[本平台默认生成]")
    private String userId;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "连连账户编号")
    private String userNo;

    @ApiModelProperty(value = "连连账户名称")
    private String username;

    @ApiModelProperty(value = "ACS银行账户")
    private String bankAccount;

    @ApiModelProperty(value = "开通银行虚拟户")
    private Boolean ifOpenBankAcct;

    @ApiModelProperty(value = "银行开户地址")
    @TableField(exist = false)
    private String  gatewayUrl;

    @ApiModelProperty(value = "连连账户信息")
    @TableField(exist = false)
    private List<AcctInfo> acctInfoList;

    @ApiModelProperty(value = "银行账户信息")
    @TableField(exist = false)
    private List<LztQueryAcctInfo> bankAcctInfoList;

    @ApiModelProperty(value = "商户名称")
    @TableField(exist = false)
    private String merName;


}