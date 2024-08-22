package com.jbp.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderFillVo对象", description = "补单Vo")
public class OrderFillVo {

    @ApiModelProperty("商品名称")
    private String name;
    @ApiModelProperty("商品图片")
    private String picUrl;

    @ApiModelProperty("补单商品数量")
    private Integer count;

    @ApiModelProperty("库存")
    private Integer goodsCount;

}
