package com.jbp.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WalletFlowVo对象", description="微信模板发送类")
public class WalletFlowVo{
    @ApiModelProperty("资金方向   收入 支出")
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
    @ApiModelProperty("账户")
    private String account;
    @ApiModelProperty("钱包名称")
    private String typeName;
    @ApiModelProperty("创建时间")
    private Date gmtModify;

}
