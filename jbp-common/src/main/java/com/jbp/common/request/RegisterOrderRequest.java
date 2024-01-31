package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="RegisterOrderRequest对象", description="下单注册")
public class RegisterOrderRequest implements Serializable {

    @ApiModelProperty(value = "用户名", required = false)
    private String username;

    @ApiModelProperty(value = "注册手机号", required = false)
    private String mobile;

    @ApiModelProperty(value = "销售账户", required = false)
    private String pAccount;

    @ApiModelProperty(value = "服务账户", required = false)
    private String rAccount;

    @ApiModelProperty(value = "服务位置", required = false)
    private Integer node;

    @ApiModelProperty(value = "注册等级", required = false)
    private Long capaId;

    @ApiModelProperty(value = "注册星级", required = false)
    private Long capaXsId;

}
