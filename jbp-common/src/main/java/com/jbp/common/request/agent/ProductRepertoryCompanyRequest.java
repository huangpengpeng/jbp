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
@ApiModel(value = "ProductRepertoryCompanyRequest对象", description = "公司调拨库存编辑对象")
public class ProductRepertoryCompanyRequest implements Serializable {

    @ApiModelProperty(value = "账号")
    @NotBlank(message = "账号不能为空")
    private String account;

    @ApiModelProperty(value = "商品id")
    @NotNull(message = "商品id不能为空")
    private Integer productId;

    @ApiModelProperty(value = "库存数")
    @NotNull(message = "库存数不能为空")
    private Integer count;

    @ApiModelProperty(value = "附言")
    @NotBlank(message = "附言不能为空")
    private String description;
}
