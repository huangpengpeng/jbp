package com.jbp.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductRepertoryVo对象", description = "商品库存Vo")
public class ProductRepertoryVo {

    @ApiModelProperty("商品名称")
    private String name;
    @ApiModelProperty("商品图片")
    private String picUrl;

    @ApiModelProperty("商品数量")
    private Integer count;


    @ApiModelProperty("商品id")
    private Integer id;

}
