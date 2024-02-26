package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import com.jbp.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_channel_identity", autoResultMap = true)
@ApiModel(value="ChannelIdentity对象", description="渠道身份信息")
public class ChannelIdentity extends BaseModel {

    public ChannelIdentity(Integer uid, String idCardNo, String realName,
                           String idCardNoFrontImg, String idCardNoBackImg, String otherJSON, String channel) {
        this.uid = uid;
        this.idCardNo = idCardNo;
        this.realName = realName;
        this.idCardNoFrontImg = idCardNoFrontImg;
        this.idCardNoBackImg = idCardNoBackImg;
        this.otherJSON = otherJSON;
        this.channel = channel;
    }

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("身份证号码")
    @TableField("id_card_no")
    private String idCardNo;

    @ApiModelProperty("真实姓名")
    @TableField("real_Name")
    private String realName;

    @ApiModelProperty("身份证正面照片")
    @TableField("id_card_no_front_img")
    private String idCardNoFrontImg;

    @ApiModelProperty("身份证反面照片")
    @TableField("id_card_no_back_img")
    private String idCardNoBackImg;

    @ApiModelProperty("其他信息")
    @TableField("other_json")
    private String otherJSON;

    @ApiModelProperty("渠道名称")
    @TableField("channel")
    private String channel;

    @ApiModelProperty("账户")
    @TableField(exist = false)
    private String account;
}
