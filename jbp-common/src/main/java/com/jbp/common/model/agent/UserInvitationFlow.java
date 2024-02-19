package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 关系留影
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@TableName("eb_user_invitation_flow")
@ApiModel(value="UserInvitationFlow对象", description="销售上下层级关系")
public class UserInvitationFlow extends BaseModel {

    private static final long serialVersionUID = 1L;

    public UserInvitationFlow(Integer uId, Integer pId, Integer level) {
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
    private Integer level;

    @ApiModelProperty("用户账户")
    @TableField(exist = false)
    private String uAccount;

    @ApiModelProperty("邀请上级账户")
    @TableField(exist = false)
    private String pAccount;

    @ApiModelProperty("等级ID")
    @TableField(exist = false)
    private Long capaId;

    @ApiModelProperty("星级ID")
    @TableField(exist = false)
    private Long xsCapaId;
}
