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
@ApiModel(value = "ActivityScoreClearingSearchRequest对象", description = "积分活动结算查询对象")
public class ActivityScoreClearingEditRequest implements Serializable {

    @NotNull(message = "id不能为空!")
    @ApiModelProperty("id")
    private Integer id;

    @NotNull(message = "分值不能为空!")
    @ApiModelProperty("分值")
    private Integer score;

    @NotNull(message = "年卡次数不能为空!")
    @ApiModelProperty("年卡")
    private Integer cardCount;
}
