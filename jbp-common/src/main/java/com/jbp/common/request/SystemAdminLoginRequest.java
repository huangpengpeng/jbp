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
    private String account;

    @ApiModelProperty(value = "后台管理员密码", example = "userPassword")
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
    private String captchaPhone;

    @ApiModelProperty(value = "微信授权code", required = false)
    private String code;

}
