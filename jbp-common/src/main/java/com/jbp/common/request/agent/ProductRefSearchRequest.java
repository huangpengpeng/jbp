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
@ApiModel(value = "ProductRefSearchRequest对象", description = "商品关联套组查询请求对象")
public class ProductRefSearchRequest implements Serializable {

    @ApiModelProperty("商品ID")
    private Integer productId;
}
