package com.jbp.common.model.b2b;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("b2b_team_user")
@ApiModel(value="TeamUser对象", description="用户团队信息")
public class TeamUser implements Serializable {

    private static final long serialVersionUID = 1L;

    public TeamUser(Integer uid, Integer tid) {
        this.uid = uid;
        this.tid = tid;
    }

    @ApiModelProperty(value = "记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("团队名称")
    @TableField("tid")
    private Integer tid;

    @ApiModelProperty("团队名称")
    @TableField(exist = false)
    private String name;

    @TableField(value = "gmtCreated", fill = FieldFill.INSERT)
    private Date gmtCreated;

    @JsonIgnore
    @TableField(value = "gmtModify", fill = FieldFill.INSERT_UPDATE, update = "now()")
    private Date gmtModify;
}
