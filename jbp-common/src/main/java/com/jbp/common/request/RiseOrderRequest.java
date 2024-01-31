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
@ApiModel(value="RiseOrderRequest对象", description="帮助下单升级")
public class RiseOrderRequest implements Serializable {

    @ApiModelProperty(value = "下单用户", required = false)
    private String account;

    @ApiModelProperty(value = "升级等级", required = false)
    private Long capaId;

    @ApiModelProperty(value = "升级星级", required = false)
    private Long capaXsId;

}
