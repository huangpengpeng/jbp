package com.jbp.common.model.b2b;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b2b_platform_wallet_flow")
@ApiModel(value="PlatformWalletFlow对象", description="平台钱包")
public class PlatformWalletFlow implements Serializable {

    public PlatformWalletFlow(String walletType, String action, String operate, String uniqueNo, String externalNo,
                              String postscript, BigDecimal amt, BigDecimal orgBalance, BigDecimal tagBalance) {
        this.walletType = walletType;
        this.action = action;
        this.operate = operate;
        this.uniqueNo = uniqueNo;
        this.externalNo = externalNo;
        this.postscript = postscript;
        this.amt = amt;
        this.orgBalance = orgBalance;
        this.tagBalance = tagBalance;
    }

    @ApiModelProperty("钱包类型")
    @TableField("walletType")
    private String walletType;

    @ApiModelProperty("资金方向")
    @TableField("action")
    private String action;

    @ApiModelProperty("操作")
    @TableField("operate")
    private String operate;

    @ApiModelProperty("流水单号")
    @TableField("uniqueNo")
    private String uniqueNo;

    @ApiModelProperty("外部单号")
    @TableField("externalNo")
    private String externalNo;

    @ApiModelProperty("附言")
    @TableField("postscript")
    private String postscript;

    @ApiModelProperty("变动金额")
    @TableField("amt")
    private BigDecimal amt;

    @ApiModelProperty("变动前余额")
    @TableField("orgBalance")
    private BigDecimal orgBalance;

    @ApiModelProperty("变动后余额")
    @TableField("tagBalance")
    private BigDecimal tagBalance;
}
