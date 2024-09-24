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
public class ActivityScoreGoodsSearchRequest implements Serializable {

    @ApiModelProperty("活动id")
    @NotNull(message = "活动id不能为空")
    private Integer activityScoreId;
}
