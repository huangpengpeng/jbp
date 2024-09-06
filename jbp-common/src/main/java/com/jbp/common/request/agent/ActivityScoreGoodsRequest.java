package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ActivityScoreGoodsRequest对象", description = "积分活动商品查询对象")
public class ActivityScoreGoodsRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "商品id不能为空")
    @ApiModelProperty("商品ids")
    private Integer productId;

    @NotNull(message = "分值不能为空")
    @ApiModelProperty("分值")
    private Integer goodsCount;
}
