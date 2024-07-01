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
@ApiModel(value = "ProductCommRequest对象", description = "商品佣金设置")
public class ProductExtConfigAddRequest implements Serializable {

    @ApiModelProperty("商品id")
    private Integer productId;

    @ApiModelProperty("商品扩展信息")
    private String content;

    @ApiModelProperty("类型")
    private String type;
}
