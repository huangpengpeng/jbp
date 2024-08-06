package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 团队名称变动因素一下三点
 * 1.新增用户
 * 2.关系变更【变更用户所有下级，往上找最近的团队头】
 * 3.团队头更新 【新增 或者 删除】
 * <p>
 * 查询团队头伞下没有团队记录的用户
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_team")
@ApiModel(value = "Team对象", description = "团队对象")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("团队名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("团队领导用户ID")
    @TableField(value = "leader_id", updateStrategy = FieldStrategy.IGNORED)
    private Integer leaderId;

    @ApiModelProperty("团队领导用户账号")
    @TableField(exist = false)
    private String account;

    @ApiModelProperty("团队领导用户昵称")
    @TableField(exist = false)
    private String nickname;

    public Team(String name, Integer leaderId) {
        this.name = name;
        this.leaderId = leaderId;
    }

}
