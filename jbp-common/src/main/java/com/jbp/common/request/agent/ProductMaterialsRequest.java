package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductMaterialsRequest对象", description = "产品物料对应仓库的编码请求对象")
public class ProductMaterialsRequest implements Serializable {

    @ApiModelProperty(value = "物料名称")
    private String materialsName;
    @ApiModelProperty("商户名称")
    private String merName;
    @ApiModelProperty(value = "商品条码")
    private String barCode;
}
