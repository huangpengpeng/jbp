package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ActivityScoreResponse对象", description="活动分值")
public class ActivityScoreResponse {
    @ApiModelProperty(value = "积分")
    private Integer score;
    @ApiModelProperty(value = "年卡")
    private Integer cardCount;
    @ApiModelProperty(value = "开始时间")
    private String startTime;
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "规则")
    private String mark;
}
