package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ActivityScoreGoodsAddRequest对象", description = "积分活动商品添加对象")
public class ActivityScoreGoodsAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "活动id不能为空")
    @ApiModelProperty("活动id")
    private Integer activityScoreId;

    @ApiModelProperty("商品对应分值信息")
    private List<ActivityScoreGoodsRequest> activityScoreGoodsList;


}
