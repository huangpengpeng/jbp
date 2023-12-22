package com.jbp.common.model.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import com.jbp.common.model.BaseModel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关系留影
 */
@Data

@Builder
@TableName("UserInvitationFlow")
@NoArgsConstructor
public class UserInvitationFlow extends BaseModel {

    public UserInvitationFlow(Long userId, Long pId, int level) {
        this.userId = userId;
        this.pId = pId;
        this.level = level;
    }

    @ApiModelProperty("用户ID")
    @TableField("userId")
    private Long userId;

    @ApiModelProperty("邀请上级")
    @TableField("pId")
    private Long pId;

    @ApiModelProperty("层级")
    @TableField("level")
    private int level;
}
