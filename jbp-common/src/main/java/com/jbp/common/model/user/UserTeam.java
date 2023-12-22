package com.jbp.common.model.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@TableName("UserTeam")
@NoArgsConstructor
@AllArgsConstructor
public class UserTeam extends BaseModel {


    private static final long serialVersionUID = -6919379039220810853L;

    @ApiModelProperty("用户ID")
    @TableField("userId")
    private Long userId;

    @ApiModelProperty("团队名")
    @TableField("name")
    private String name;

    @ApiModelProperty("是否团队头")
    @TableField("ifHead")
    private Boolean ifHead;

    @ApiModelProperty("用户账户")
    @TableField(exist = false)
    private String accountNo;

    public static enum Constants {
        默认空
    }
}
