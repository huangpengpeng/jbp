package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "UserOfflineSubsidyRequest对象", description = "用户线下补助请求对象")
public class UserOfflineSubsidyRequest implements Serializable {

    @ApiModelProperty("用户账号")
    private String account;
    @ApiModelProperty("省份")
    private Integer provinceId;
    @ApiModelProperty("城市")
    private Integer cityId;
    @ApiModelProperty("区域")
    private Integer areaId;
    @ApiModelProperty("团队id")
    private Integer teamId;
}
