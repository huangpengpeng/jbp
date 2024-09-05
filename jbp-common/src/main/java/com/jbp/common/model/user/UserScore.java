package com.jbp.common.model.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_user_score")
@ApiModel(value = "UserScore对象", description = "用户分数记录表")
public class UserScore extends BaseModel {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "分数")
    private Integer score;

    @ApiModelProperty(value = "用户账号")
    @TableField(exist = false)
    private String account;

    @ApiModelProperty(value = "用户昵称")
    @TableField(exist = false)
    private String nickname;

    @ApiModelProperty(value = "用户手机号")
    @TableField(exist = false)
    private String phone;

    @ApiModelProperty(value = "用户团队")
    @TableField(exist = false)
    private String teamName;


}
