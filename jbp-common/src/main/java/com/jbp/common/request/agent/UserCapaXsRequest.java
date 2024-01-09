package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaXs;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserCapaXsRequest对象", description = "用户星级请求对象")
public class UserCapaXsRequest implements Serializable {
    @ApiModelProperty("用户账号")
    private String account;
    @ApiModelProperty("等级ID")
    @TableField("capa_id")
    private Long capaId;
}
