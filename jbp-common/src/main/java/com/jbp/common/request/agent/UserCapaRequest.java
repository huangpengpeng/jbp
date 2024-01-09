package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserCapaRequest对象", description = "用户等级请求对象")
public class UserCapaRequest {
    @ApiModelProperty("用户账号")
    private String account;
    @ApiModelProperty("等级ID")
    @TableField("capa_id")
    private Long capaId;
}
