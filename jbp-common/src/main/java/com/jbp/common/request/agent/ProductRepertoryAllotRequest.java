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
@ApiModel(value = "ProductRepertoryAllotRequest对象", description = "调拨库存请求对象")
public class ProductRepertoryAllotRequest implements Serializable {

    @ApiModelProperty("调拨账号")
    private String fromAccount;

    @ApiModelProperty("调拨商品")
    private Integer productId;

    @ApiModelProperty("调拨数量")
    private Integer count;

    @ApiModelProperty("接收账号")
    private String toAccount;

}
