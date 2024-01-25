package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductCommRequest对象", description = "商品佣金设置")
public class ProductCommRequest implements Serializable {

    @NotNull(message = "产品ID不能为空")
    @ApiModelProperty("产品ID")
    private Integer productId;

    @NotNull(message = "佣金类型不能为空")
    @ApiModelProperty("佣金类型")
    private Integer type;

    @NotNull(message = "系数不能为空")
    @ApiModelProperty("系数")
    private BigDecimal scale;

    @NotBlank(message = "佣金规则不能为空")
    @ApiModelProperty("规则")
    private String rule;

    @NotNull(message = "状态不能为空")
    @ApiModelProperty("状态")
    private Boolean status;
}
