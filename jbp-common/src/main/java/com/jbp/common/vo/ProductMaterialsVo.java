package com.jbp.common.vo;

import com.jbp.common.model.agent.ProductMaterials;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductMaterialsVo对象", description = "发货单物料Vo")
public class ProductMaterialsVo extends ProductMaterials implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "单个物料总成本价")
    private BigDecimal totalCostPrice;



}
