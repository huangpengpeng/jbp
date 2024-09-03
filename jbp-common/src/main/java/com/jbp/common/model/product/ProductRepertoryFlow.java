package com.jbp.common.model.product;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_product_repertory_flow")
@ApiModel(value="ProductRepertoryFlow对象", description="用户商品库存明细表")
public class ProductRepertoryFlow extends BaseModel {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "商品id")
    private Integer productId;

    @ApiModelProperty(value = "数量")
    private Integer count;

    @ApiModelProperty(value = "说明")
    private String description;

    @ApiModelProperty(value = "订单号")
    private String orderSn;

    @ApiModelProperty(value = "时间")
    private Date time;

    @ApiModelProperty(value = "类型（发货，供货，订货,调拨，取消订单）")
    private String type;

    @ApiModelProperty(value = "剩余库存")
    private Integer surplusCount;

    @ApiModelProperty(value = "库存变动方向")
    private String kind;

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


}
