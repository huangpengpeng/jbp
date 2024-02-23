package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserCapaAddRequest对象", description = "用户等级添加请求对象")
public class UserCapaAddRequest implements Serializable {

    @NotBlank(message = "用户账号不能为空")
    @ApiModelProperty("用户账号")
    private String account;

    @NotNull(message = "用户等级不能为空")
    @ApiModelProperty("等级ID")
    private Long capaId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("系统描述")
    private String description;
}
