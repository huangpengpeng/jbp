package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductMaterialsAddRequest对象", description = "产品物料对应仓库的编码添加对象")
public class ProductMaterialsEditRequest  implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @NotBlank(message = "商品条码不能为空")
    @ApiModelProperty(value = "商品条码")
    private String barCode;

    @NotBlank(message = "物料名称不能为空")
    @ApiModelProperty(value = "物料名称")
    private String materialsName;

    @ApiModelProperty(value = "物料数量")
    private Integer materialsQuantity;

    @ApiModelProperty(value = "物料成本价")
    private BigDecimal materialsPrice;

    @NotBlank(message = "物料编码不能为空")
    @ApiModelProperty(value = "物料编码")
    private String materialsCode;

    @ApiModelProperty(value = "供应商名称")
    private String supplyName;
}
