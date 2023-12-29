package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_channel_wallet", autoResultMap = true)
@ApiModel(value="ChannelWallet对象", description="渠道钱包")
public class ChannelWallet extends BaseModel {

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("渠道商编")
    @TableField("channelCode")
    private String channelCode;

    @ApiModelProperty("支付商编")
    @TableField("walletCode")
    private String walletCode;

    @ApiModelProperty("渠道商编状态")
    @TableField("status")
    private String status;

    @ApiModelProperty("商编类型【企业 个人】")
    @TableField("type")
    private String type;

    @ApiModelProperty("审核拒绝原因")
    @TableField("refuseMsg")
    private String refuseMsg;

    @ApiModelProperty("开户请求订单号")
    @TableField("requestNo")
    private String requestNo;

    @ApiModelProperty("渠道名称")
    @TableField("channel")
    private String channel;
}
