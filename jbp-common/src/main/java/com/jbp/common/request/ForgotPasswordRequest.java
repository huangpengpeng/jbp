package com.jbp.common.request;

import com.jbp.common.constants.RegularConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ForgotPasswordRequest对象", description = "忘记密码请求对象")
public class ForgotPasswordRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "账号")
    @NotBlank(message = "账号不能为空")
    private String account;
    @ApiModelProperty(value = "密码")
    @NotBlank(message = "修改密码不能为空")
    private String password;

    @ApiModelProperty(value = "手机号")
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @ApiModelProperty(value = "验证码", required = true)
    @Pattern(regexp = RegularConstants.VALIDATE_CODE_NUM_SIX, message = "验证码格式错误，验证码必须为6位数字")
    private String captcha;
}
