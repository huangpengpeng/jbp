package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderFillSearchRequest对象", description = "订单补单表查询对象")
public class OrderFillSearchRequest implements Serializable {

    @ApiModelProperty(value = "下单人账号")
    private String oAccount;

    @ApiModelProperty(value = "下单人昵称")
    private String oNickname;

    @ApiModelProperty(value = "订单号")
    private String orderNo;
}
