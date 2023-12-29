package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_channel_wallet_flow", autoResultMap = true)
@ApiModel(value="ChannelWalletFlow对象", description="渠道钱包明细")
public class ChannelWalletFlow extends BaseModel {

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("渠道商编")
    @TableField("channelCode")
    private String channelCode;

    @ApiModelProperty("资金方向 收入 支出")
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

    @ApiModelProperty("渠道名称")
    @TableField("channel")
    private String channel;
}
