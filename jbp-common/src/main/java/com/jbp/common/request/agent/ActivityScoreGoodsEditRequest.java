package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ActivityScoreGoodsEditRequest对象", description = "积分活动商品编辑对象")
public class ActivityScoreGoodsEditRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空！")
    @ApiModelProperty("id")
    private Integer id;

    @NotNull(message = "分值不能为空！")
    @ApiModelProperty("分值")
    private BigDecimal goodsCount;
}
