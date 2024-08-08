package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductRefAddRequest对象", description = "商品关联套组增加请求对象")
public class ProductRefAddRequest implements Serializable {



    @ApiModelProperty(value = "数量")
    @NotNull(message = "商品数量不能为空")
    private Integer count;

    @ApiModelProperty(value = "价格")
    @NotNull(message = "商品价格不能为空")
    private BigDecimal price;

    @ApiModelProperty(value = "关联套组")
    @NotNull(message = "关联套组礼包不能为空")
    private Integer refProductId;
}
