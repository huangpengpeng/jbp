package com.jbp.common.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import com.jbp.common.constants.RegularConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * PC登录请求对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
public class SystemAdminLoginRequest {
    @ApiModelProperty(value = "后台管理员账号", example = "userName")
    @NotEmpty(message = "账号 不能为空")
    @Length(max = 32, message = "账号长度不能超过32个字符")
    private String account;

    @ApiModelProperty(value = "后台管理员密码", example = "userPassword")
    @NotEmpty(message = "密码 不能为空")
    @Length(min = 6, max = 30 ,message = "密码长度在6-30个字符")
    private String pwd;

    @ApiModelProperty(value = "key", required = false)
    private String key;

    @ApiModelProperty(value = "code", required = false)
    private String reqCode;
    
    @ApiModelProperty(value = "mfa", required = false)
    private String reqMfa;
    
    @ApiModelProperty(value = "手机号", required = false)
    private String phone;

    @ApiModelProperty(value = "手机验证码", required = false)
    @Pattern(regexp = RegularConstants.VALIDATE_CODE_NUM_SIX, message = "验证码格式错误，验证码必须为6位数字")
    private String captchaPhone;

}
