package com.jbp.common.model.b2b;

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

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b2b_user_invitation")
@ApiModel(value="UserRelation对象", description="服务关系上下级")
public class UserRelation  implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableField("userId")
    private Long userId;

    @ApiModelProperty("上级用户ID")
    @TableField("pId")
    private Long pId;

    @ApiModelProperty("节点")
    @TableField("node")
    private Integer node;

    @TableField(value = "gmtCreated", fill = FieldFill.INSERT)
    private Date gmtCreated;

    @JsonIgnore
    @TableField(value = "gmtModify", fill = FieldFill.INSERT_UPDATE, update = "now()")
    private Date gmtModify;
}
