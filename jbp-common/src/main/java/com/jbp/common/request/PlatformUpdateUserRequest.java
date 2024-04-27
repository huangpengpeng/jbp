package com.jbp.common.request;

import com.jbp.common.exception.CrmebException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PlatformUpdateUserRequest对象", description = "修改密码请求对象")
public class PlatformUpdateUserRequest implements Serializable {
    @NotNull(message = "用户编号不能为空")
    @ApiModelProperty(value = "用户id")
    private Integer id;

    @ApiModelProperty(value = "用户密码")
    private String pwd;

    @ApiModelProperty(value = "性别，0未知，1男，2女，3保密")
    private Integer sex;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "国家，中国CN，其他OTHER")
    private String country;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "交易密码")
    private String payPwd;

    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "是否开店")
    private Boolean openShop;

//    public void setPhone(String phone) {
//        if (phone != null && phone.matches("^1\\d{2}\\*{4}\\d{4}$")) {
//            // 如果是假数据，则将其设置为null
//         //   this.phone = null;
//        } else if (isValidPhoneNumber(phone)) {
//            // 如果手机号码格式正确，则将其设置为输入的值
//            this.phone = phone;
//        } else {
//            throw new CrmebException("手机号格式错误");
//        }
//    }

//    private boolean isValidPhoneNumber(String phone) {
//        // 此处可以编写更复杂的逻辑以验证手机号格式，例如正则表达式等
//        return phone != null && phone.matches("^(?:1[3-9]\\d{9}|)$");
//    }
}
