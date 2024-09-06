package com.jbp.common.model.agent;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 积分活动
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_activity_score_goods", autoResultMap = true)
@ApiModel(value = "ActivityScoreGoods对象", description = "积分活动商品")
public class ActivityScoreGoods extends BaseModel {

    @ApiModelProperty("活动id")
    @TableField("activity_score_id")
    private Integer activityScoreId;

    @ApiModelProperty("活动商品id")
    @TableField("activity_score_goods_id")
    private Integer activityScoreGoodsId;

    @ApiModelProperty("活动商品分数")
    @TableField("goods_count")
    private Integer goodsCount;

    @ApiModelProperty("商品图")
    @TableField(exist = false)
    private String image;

    @ApiModelProperty("商品名称")
    @TableField(exist = false)
    private String productName;

    @ApiModelProperty("商品价格")
    @TableField(exist = false)
    private BigDecimal price;

    @ApiModelProperty("商品库存")
    @TableField(exist = false)
    private Integer stock;
}
