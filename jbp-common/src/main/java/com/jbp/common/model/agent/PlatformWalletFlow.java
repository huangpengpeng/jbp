package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_platform_wallet_flow")
@Builder
@ApiModel(value="PlatformWalletFlow对象", description="平台钱包")
public class PlatformWalletFlow extends BaseModel {
    private static final long serialVersionUID = 1L;

    public static enum OperateEnum {
        调账, 分佣
    }

    public static enum ActionEnum {
        收入, 支出
    }

    @ApiModelProperty("钱包类型")
    @TableField("wallet_type")
    private Integer walletType;

    @ApiModelProperty("资金方向")
    @TableField("action")
    private String action;

    @ApiModelProperty("操作")
    @TableField("operate")
    private String operate;

    @ApiModelProperty("流水单号")
    @TableField("unique_no")
    private String uniqueNo;

    @ApiModelProperty("外部单号")
    @TableField("external_no")
    private String externalNo;

    @ApiModelProperty("附言")
    @TableField("postscript")
    private String postscript;

    @ApiModelProperty("变动金额")
    @TableField("amt")
    private BigDecimal amt;

    @ApiModelProperty("变动前余额")
    @TableField("org_balance")
    private BigDecimal orgBalance;

    @ApiModelProperty("变动后余额")
    @TableField("tag_balance")
    private BigDecimal tagBalance;
}