package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "TeamUserRequest对象", description = "团队用户请求对象")
public class TeamUserRequest implements Serializable {
    @ApiModelProperty("团队名称")
    @TableField("tid")
    private Integer tid;
    @ApiModelProperty("用户账号")
    private String account;
    @ApiModelProperty("是否为团队头")
    private Integer teamLeader;
    @ApiModelProperty("用户昵称")
    private String nickname;
}
