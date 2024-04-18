package com.jbp.common.request;

import com.alibaba.druid.sql.visitor.functions.Insert;
import com.jbp.common.request.agent.UserCapaAddRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RegisterPhoneRequest对象", description = "后台用户注册对象")
public class
RegisterPhoneRequest implements Serializable {
    @ApiModelProperty(value = "用户名")
    @NotNull(message = "用户名不能为空")
    private String username;
    @ApiModelProperty(value = "手机号")

    @NotBlank(message = "手机号码不能为空", groups = {Insert.class})
    @NotNull(message = "手机号不能为空", groups = {Insert.class})
//    @Length(min = 11, max = 11, message = "手机号只能为11位")
//    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$", message = "手机号格式有误")
    private String phone;

    @ApiModelProperty("账号")
    @NotBlank(message = "账号不能为空")
    private String account;

    @ApiModelProperty("等级")
    private UserCapaTemplateRequest userCapaTemplateRequest;

    @ApiModelProperty("邀请上级账号")
    private String regionPAccount;

    @ApiModelProperty("邀请上级账号节点")
    private Integer regionPNode;

    @ApiModelProperty("邀请上级账号")
    private String invitationPAccount;

    @ApiModelProperty(value = "用户密码")
    @NotBlank(message = "请设置初始化密码")
    private String pwd;

}
