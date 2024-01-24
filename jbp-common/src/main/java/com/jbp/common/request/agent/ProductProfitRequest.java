package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductProfitRequest对象", description = "商品配套设置")
public class ProductProfitRequest implements Serializable {

    @NotNull(message = "产品ID不能为空")
    @ApiModelProperty("产品ID")
    private Integer productId;

    @NotNull(message = "配套类型不能为空")
    @ApiModelProperty("配套类型")
    private Integer type;

    @NotBlank(message = "配套规则不能为空")
    @ApiModelProperty("规则")
    private String rule;

    @NotBlank(message = "状态不能为空")
    @ApiModelProperty("状态")
    private Boolean status;
}
