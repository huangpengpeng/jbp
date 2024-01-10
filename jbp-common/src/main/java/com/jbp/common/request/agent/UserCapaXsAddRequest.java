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
@ApiModel(value = "UserCapaXsAddRequest对象", description = "用户星级添加请求对象")
public class UserCapaXsAddRequest {
    @ApiModelProperty("用户账号")
    private String account;
    @ApiModelProperty("等级ID")
    @TableField("capa_id")
    private Long capaId;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("系统描述")
    private String description;
}
