package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ProductRuleEditRequest对象", description="编辑商品供货规则请求对象")
public class ProductRuleEditRequest implements Serializable {

    @ApiModelProperty(value = "复制商品地址")
    @NotNull(message = "商品id不能为空")
    private Integer id;

    @ApiModelProperty(value = "供货规则")
    private String supplyRule;

    @ApiModelProperty(value = "运费承担")
    private String freightAssume;
}
