package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import com.jbp.common.model.VersionModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@TableName("eb_wallet_give_plan")
@ApiModel(value = "WalletGivePlan对象", description = "用户钱包奖励计划")
public class WalletGivePlan extends VersionModel {

    private static final long serialVersionUID = 1L;

    public WalletGivePlan(Integer uid, String account, String walletName, Integer walletType, BigDecimal amt,
                          String externalNo, String postscript, String planTime) {
        this.uid = uid;
        this.account = account;
        this.walletName = walletName;
        this.walletType = walletType;
        this.amt = amt;
        this.externalNo = externalNo;
        this.postscript = postscript;
        this.planTime = planTime;
        this.status = "未使用";
    }

    @ApiModelProperty("用户ID")
    private Integer uid;

    @ApiModelProperty("账户")
    private String account;

    @ApiModelProperty("钱包名称")
    private String walletName;

    @ApiModelProperty("钱包类型")
    private Integer walletType;

    @ApiModelProperty("变动金额")
    private BigDecimal amt;

    @ApiModelProperty("外部单号")
    private String externalNo;

    @ApiModelProperty("附言")
    private String postscript;

    @ApiModelProperty("计划赠送时间")
    private String planTime;

    @ApiModelProperty("赠送时间")
    private String updateTime;

    @ApiModelProperty("状态  未使用 已使用 已取消")
    private String status;


}
