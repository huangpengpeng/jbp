package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_product_materials", autoResultMap = true)
@ApiModel(value = "ProductMaterials对象  merId+barCode+materialsCode 唯一", description = "产品物料对应仓库的编码")
public class ProductMaterials extends BaseModel {

    public ProductMaterials(Integer merId, String barCode, String materialsName,
                            Integer materialsQuantity, BigDecimal materialsPrice, String materialsCode, String supplyName) {
        this.merId = merId;
        this.barCode = barCode;
        this.materialsName = materialsName;
        this.materialsQuantity = materialsQuantity;
        this.materialsPrice = materialsPrice;
        this.materialsCode = materialsCode;
        this.supplyName = supplyName;
    }

    @ApiModelProperty(value = "商户")
    private Integer merId;

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

    @ApiModelProperty("商户名称")
    @TableField(exist = false)
    private String merName;

    @ApiModelProperty("供应商")
    private String supplyName;

}
