package com.jbp.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PlatformWalletFlowVo", description = "平台积分详情vo对象")
public class PlatformWalletFlowVo {


    @ApiModelProperty("资金方向")
    private String action;

    @ApiModelProperty("操作")
    private String operate;

    @ApiModelProperty("流水单号")
    private String uniqueNo;

    @ApiModelProperty("外部单号")
    private String externalNo;

    @ApiModelProperty("附言")
    private String postscript;

    @ApiModelProperty("变动金额")
    private BigDecimal amt;

    @ApiModelProperty("变动前余额")
    private BigDecimal orgBalance;

    @ApiModelProperty("变动后余额")
    private BigDecimal tagBalance;
    @ApiModelProperty("钱包名称")
    private String typeName;
}
