package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_relation")
@ApiModel(value = "UserRelation对象", description = "服务关系上下级")
public class UserRelation extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableField("uId")
    private Integer uId;

    @ApiModelProperty("上级用户ID")
    @TableField("pId")
    private Integer pId;

    @ApiModelProperty("节点")
    @TableField("node")
    private Integer node;

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

    @ApiModelProperty("等级ID")
    @TableField(exist = false)
    private String uCapaName;

    @ApiModelProperty("星级ID")
    @TableField(exist = false)
    private String uCapaXsName;

    @ApiModelProperty("上级等级")
    @TableField(exist = false)
    private String pCapaName;

    @ApiModelProperty("上级星级")
    @TableField(exist = false)
    private String pCapaXsName;
}
