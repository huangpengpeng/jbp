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
@ApiModel(value = "UserJdRequest对象", description = "用户京东账号请求对象")
public class UserJdRequest implements Serializable {
    @ApiModelProperty("用户账号")
    private String account;
    @ApiModelProperty("用户名称")
    private String name;
}
