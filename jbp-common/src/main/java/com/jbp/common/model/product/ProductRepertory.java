package com.jbp.common.model.product;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;



@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_product_repertory")
@ApiModel(value="ProductRepertory对象", description="用户商品库存表")
public class ProductRepertory extends BaseModel {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "数量")
    private Integer count;

    @ApiModelProperty(value = "商品名称")
    @TableField(exist = false)
    private String productName;

    @ApiModelProperty(value = "商品编码")
    @TableField(exist = false)
    private String barCode;

    @ApiModelProperty(value = "用户账号")
    @TableField(exist = false)
    private String account;

    @ApiModelProperty(value = "用户昵称")
    @TableField(exist = false)
    private String nickname;

    @ApiModelProperty(value = "团队名称")
    @TableField(exist = false)
    private String teamName;

    @Version
    @TableField(value = "version", fill = FieldFill.INSERT)
    private Integer version = 1;





}
