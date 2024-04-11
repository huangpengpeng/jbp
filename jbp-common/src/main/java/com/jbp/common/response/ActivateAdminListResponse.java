package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="AccountCapaResponse对象", description="账户等级对象")
public class ActivateAdminListResponse {
    @ApiModelProperty(value = "账户")
    private String account;
    @ApiModelProperty(value = "等级编号")
    private Integer capaId;
    @ApiModelProperty(value = "等级图标地址")
    private String iconUrl;
}
