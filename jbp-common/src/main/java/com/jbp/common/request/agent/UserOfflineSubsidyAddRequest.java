package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "UserOfflineSubsidyAddRequest对象", description = "用户线下补贴添加请求对象")
public class UserOfflineSubsidyAddRequest implements Serializable {

    @ApiModelProperty("用户账号")
    @NotBlank(message = "用户账号不能为空")
    private String account;

//    @ApiModelProperty("省份")
//    @NotBlank(message = "省份不能为空")
//    private String province;

    @ApiModelProperty(value = "省份ID")
    @NotNull(message = "省份ID不能为空")
    private Integer provinceId;

//    @ApiModelProperty("城市")
//    @NotBlank(message = "城市不能为空")
//    private String city;

    @ApiModelProperty(value = "城市id")
    @NotNull(message = "城市id不能为空")
    private Integer cityId;

//    @ApiModelProperty("区域")
//    private String area;

    @ApiModelProperty(value = "区/县id")
//    @NotNull(message = "区/县id不能为空")
    private Integer areaId;

}
