package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_clearing_relation_flow", autoResultMap = true)
@ApiModel(value="ClearingRelationFlow对象", description="结算服务关系")
public class ClearingRelationFlow extends BaseModel {

    private static final long serialVersionUID = 1L;

    public ClearingRelationFlow(Long clearingId, Integer uId, Integer pId, Integer level, Integer node) {
        this.clearingId = clearingId;
        this.uId = uId;
        this.pId = pId;
        this.level = level;
        this.node = node;
    }

    @ApiModelProperty("结算ID")
    @TableField("clearingId")
    private Long clearingId;

    @ApiModelProperty("用户ID")
    @TableField("uId")
    private Integer uId;

    @ApiModelProperty("邀请上级")
    @TableField("pId")
    private Integer pId;

    @ApiModelProperty("层级")
    @TableField("level")
    private Integer level;

    @ApiModelProperty("位置自己往上查询，自己在上级的0  区 或者 1区")
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
}
