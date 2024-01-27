package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 业务关系网
 */


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_invitation")
@ApiModel(value="UserInvitation对象", description="销售上下级关系")
public class UserInvitation extends BaseModel {

    private static final long serialVersionUID=1L;

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

    public Integer getRealPid(){
        return  getMId() != null ? getMId() : getPId();
    }
}
