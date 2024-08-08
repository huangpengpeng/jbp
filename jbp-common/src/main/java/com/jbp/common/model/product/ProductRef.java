package com.jbp.common.model.product;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
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
@TableName("eb_product_ref")
@ApiModel(value="ProductRef对象", description="商品关联套组表")
public class ProductRef  extends BaseModel {

    private static final long serialVersionUID=1L;


    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "数量")
    private Integer count;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "关联套组")
    private Integer refProductId;

}
