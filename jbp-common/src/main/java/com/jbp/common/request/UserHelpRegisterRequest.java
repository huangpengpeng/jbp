package com.jbp.common.request;

import com.jbp.common.exception.CrmebException;
import com.jbp.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserHelpRegisterRequest", description = "帮忙注册")
public class UserHelpRegisterRequest implements Serializable {

    @ApiModelProperty(value = "昵称", required = true)
    @NotBlank(message = "昵称不能为空")
    private String username;

    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @ApiModelProperty(value = "邀请上级", required = true)
    @NotBlank(message = "销售上级不能为空")
    private String paccount;

    @ApiModelProperty(value = "服务上级", required = true)
    @NotBlank(message = "服务上级不能为空")
    private String raccount;

    @ApiModelProperty(value = "市场位置", required = true)
    @NotNull(message = "市场位置不能为空")
    @Min(0)
    @Max(1)
    private Integer node;

}
