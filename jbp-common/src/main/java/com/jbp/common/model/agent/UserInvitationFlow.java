package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 关系留影
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_invitation_flow")
@ApiModel(value="UserInvitationFlow对象", description="销售上下层级关系")
public class UserInvitationFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    public UserInvitationFlow(Integer uId, Integer pId, int level) {
        this.uId = uId;
        this.pId = pId;
        this.level = level;
    }

    @ApiModelProperty("用户ID")
    @TableField("uId")
    private Integer uId;

    @ApiModelProperty("邀请上级")
    @TableField("pId")
    private Integer pId;

    @ApiModelProperty("层级")
    @TableField("level")
    private int level;

    @TableField(value = "gmtCreated", fill = FieldFill.INSERT)
    private Date gmtCreated;

    @JsonIgnore
    @TableField(value = "gmtModify", fill = FieldFill.INSERT_UPDATE, update = "now()")
    private Date gmtModify;
}
