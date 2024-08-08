package com.jbp.common.model.product;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_product_repertory")
@ApiModel(value="ProductRepertory对象", description="用户商品库存表")
public class ProductRepertory extends BaseModel {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户id")
    private Integer uId;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "数量")
    private Integer count;



}
