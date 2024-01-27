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
 * 关系留影
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_relation_flow")
@ApiModel(value = "UserRelationFlow对象", description = "服务关系上下层级关系")
public class UserRelationFlow extends BaseModel {

    private static final long serialVersionUID = 1L;

    public UserRelationFlow(Integer uId, Integer pId, int level, int node) {
        this.uId = uId;
        this.pId = pId;
        this.level = level;
        this.node = node;
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

    @ApiModelProperty("位置自己往上查询，自己在上级的0  区 或者 1区")
    @TableField("node")
    private int node;

}

