package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 用户等级
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_capa")
@ApiModel(value="UserCapa对象", description="用户等级")
public class UserCapa extends BaseModel {

    @ApiModelProperty("用户ID")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("等级ID")
    @TableField("capa_id")
    private Long capaId;

    @ApiModelProperty("账户")
    @TableField(exist = false)
    private String account;

    @ApiModelProperty("等级名称")
    @TableField(exist = false)
    private String capaName;

    @ApiModelProperty("等级头像")
    @TableField(exist = false)
    private String capaUrl;
}
