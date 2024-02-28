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
 * 业务关系网
 */


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_invitation")
@ApiModel(value = "UserInvitation对象", description = "销售上下级关系")
public class UserInvitation extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableField("uId")
    private Integer uId;

    @ApiModelProperty("邀请上级")
    @TableField("pId")
    private Integer pId;

    @ApiModelProperty("转挂上级")
    @TableField("mId")
    private Integer mId;

    @ApiModelProperty("强制绑定")
    @TableField("ifForce")
    private Boolean ifForce;

    @ApiModelProperty("用户账户")
    @TableField(exist = false)
    private String uAccount;

    @ApiModelProperty("用户真实姓名")
    @TableField(exist = false)
    private String uRealName;

    @ApiModelProperty("邀请上级账户")
    @TableField(exist = false)
    private String pAccount;

    @ApiModelProperty("邀请上级用户真实姓名")
    @TableField(exist = false)
    private String pRealName;

    @ApiModelProperty("转挂上级账户")
    @TableField(exist = false)
    private String mAccount;

    @ApiModelProperty("转挂用户真实姓名")
    @TableField(exist = false)
    private String mRealName;

    public Integer getRealPid() {
        return getMId() != null ? getMId() : getPId();
    }
}
