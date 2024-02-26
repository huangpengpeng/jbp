package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 四要素认证卡【线下打款的银行卡和三方支付无关】
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_channel_card", autoResultMap = true)
@ApiModel(value="ChannelCard对象", description="渠道银行卡")
public class ChannelCard extends BaseModel {

    private static final long serialVersionUID = 4856535195748963537L;

    public ChannelCard(Integer uid, String bankId, String bankCardNo,
                       String bankName, String branchId, String branchName, String type,
                       String province, String city, String phone, String channel) {
        this.uid = uid;
        this.bankId = bankId;
        this.bankCardNo = bankCardNo;
        this.bankName = bankName;
        this.branchId = branchId;
        this.branchName = branchName;
        this.type = type;
        this.province = province;
        this.city = city;
        this.phone = phone;
        this.channel = channel;
    }

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("渠道商编")
    @TableField("channel_code")
    private String channelCode;

    @ApiModelProperty("银行编码  总行编号")
    @TableField("bank_id")
    private String bankId;

    @ApiModelProperty("银行卡号")
    @TableField("bank_card_no")
    private String bankCardNo;

    @ApiModelProperty("银行名称")
    @TableField("bank_name")
    private String bankName;

    @ApiModelProperty("开户行支行编码  联行卡号")
    @TableField("branch_id")
    private String branchId;

    @ApiModelProperty("开户行支行名字")
    @TableField("branch_name")
    private String branchName;

    @ApiModelProperty("银行卡类型")
    @TableField("type")
    private String type;

    @ApiModelProperty("开户省份")
    @TableField("province")
    private String province;

    @ApiModelProperty("开户城市")
    @TableField("city")
    private String city;

    @ApiModelProperty("银行卡预留手机号")
    @TableField("phone")
    private String phone;

    @ApiModelProperty("渠道名称")
    @TableField("channel")
    private String channel;

    @ApiModelProperty("账户")
    @TableField(exist = false)
    private String account;
}
