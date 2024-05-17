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
@ApiModel(value = "PlatformOrderAddressEditRequest对象", description = "订单修改地址请求对象")
public class PlatformOrderAddressEditRequest implements Serializable {

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "收货人姓名")
    @NotBlank(message = "收货人姓名不能为空")
    private String realName;

    @ApiModelProperty(value = "收货人电话")
    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = RegularConstants.PHONE_TWO, message = "请输入正确的手机号")
    private String userPhone;

    @ApiModelProperty(value = "收货省")
    @NotBlank(message = "收货省不能为空")
    private String province;

    @ApiModelProperty(value = "收货市")
    @NotBlank(message = "收货市不能为空")
    private String city;

    @ApiModelProperty(value = "收货区")
    @NotBlank(message = "收货区/县不能为空")
    private String district;

    @ApiModelProperty(value = "收货街道")
//    @NotBlank(message = "收货街道不能为空")
    private String street;

    @ApiModelProperty(value = "收货详细地址")
    @NotBlank(message = "收货详细地址不能为空")
    private String address;
}
