package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductMaterialsAddRequest对象", description = "产品物料对应仓库的编码添加对象")
public class ProductMaterialsAddRequest {

    @ApiModelProperty(value = "商品条码")
    private String barCode;

    @ApiModelProperty(value = "物料名称")
    private String materialsName;

    @ApiModelProperty(value = "物料数量")
    private Integer materialsQuantity;

    @ApiModelProperty(value = "物料成本价")
    private BigDecimal materialsPrice;

    @ApiModelProperty(value = "物料编码")
    private String materialsCode;
}
