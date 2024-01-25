package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_product_materials", autoResultMap = true)
@ApiModel(value="ProductMaterials对象  barCode+materialsCode 唯一", description="产品物料对应仓库的编码")
public class ProductMaterials extends BaseModel {

    @ApiModelProperty(value = "商品条码")
    private String barCode;

    @ApiModelProperty(value = "物料名称")
    private String materialsName;

    @ApiModelProperty(value = "物料数量")
    private Integer materialsQuantity;

    @ApiModelProperty(value = "物料编码")
    private String materialsCode;
}
