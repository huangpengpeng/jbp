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
@TableName(value = "eb_clearing_invitation_flow", autoResultMap = true)
@ApiModel(value="ClearingInvitationFlow对象", description="结算邀请关系")
public class ClearingInvitationFlow extends BaseModel {

    public ClearingInvitationFlow(Long clearingId, Integer uId, Integer pId, Integer level) {
        this.clearingId = clearingId;
        this.uId = uId;
        this.pId = pId;
        this.level = level;
    }

    @ApiModelProperty("结算ID")
    @TableField("clearingId")
    private Long clearingId;

    @ApiModelProperty("用户ID")
    @TableField("uId")
    private Integer uId;

    @ApiModelProperty("邀请上级")
    @TableField("pId")
    private Integer pId;

    @ApiModelProperty("层级")
    @TableField("level")
    private Integer level;
    @ApiModelProperty("用户账户")
    @TableField(exist = false)
    private String uAccount;

    @ApiModelProperty("用户昵称")
    @TableField(exist = false)
    private String uNickName;

    @ApiModelProperty("上级用户账户")
    @TableField(exist = false)
    private String pAccount;

    @ApiModelProperty("上级用户昵称")
    @TableField(exist = false)
    private String pNickName;
}
