package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 用户星级快照
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_capa_xs_snapshot")
@ApiModel(value="UserCapaXsSnapshot对象", description="用户星级快照")
public class UserCapaXsSnapshot extends BaseModel {

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("等级ID")
    @TableField("capa_id")
    private Long capaId;

    @ApiModelProperty("类型")
    @TableField("type")
    private String type;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("系统描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("账户名称")
    @TableField(exist = false)
    private String account;

    @ApiModelProperty("等级名称")
    @TableField(exist = false)
    private String capaName;

    @ApiModelProperty("等级头像")
    @TableField(exist = false)
    private String capaUrl;

    public static enum Constants {
         升级, 降级, 删除
    }
}
