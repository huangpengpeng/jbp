package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 邀请关系网跳转
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_invitation_jump")
@ApiModel(value = "UserInvitationJump对象", description = "销售上下级关系")
public class UserInvitationJump extends BaseModel {

    public UserInvitationJump(Integer uId, Integer pId, Integer orgPid) {
        this.uId = uId;
        this.pId = pId;
        this.orgPid = orgPid;
    }

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableField("uId")
    private Integer uId;

    @ApiModelProperty("当前上级")
    @TableField("pId")
    private Integer pId;

    @ApiModelProperty("原来上级")
    @TableField("orgPid")
    private Integer orgPid;
}
