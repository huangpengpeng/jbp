package com.jbp.common.request.agent;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 修改密码请求对象
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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PasswordResetRequest对象", description = "修改登录密码和交易密码请求对象")
public class PasswordResetRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "密码")
    private String pwd;

    @ApiModelProperty("交易密码")
    private String payPwd;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty(value = "验证码")
    // @Pattern(regexp = RegularConstants.VALIDATE_CODE_NUM_SIX, message = "验证码格式错误，验证码必须为6位数字")
//    @JsonProperty(value = "captcha")
    private String validateCode;
}
