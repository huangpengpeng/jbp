package com.jbp.common.model.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 业务关系网
 */
@Data

@Builder
@TableName("UserInvitation")
@NoArgsConstructor
@AllArgsConstructor
public class UserInvitation extends BaseModel {

    @ApiModelProperty("用户ID")
    @TableField("userId")
    private Long userId;

    @ApiModelProperty("邀请上级")
    @TableField("pId")
    private Long pId;

    @ApiModelProperty("转挂上级")
    @TableField("mId")
    private Long mId;

    @ApiModelProperty("强制绑定")
    @TableField("ifForce")
    private Boolean ifForce;
}
