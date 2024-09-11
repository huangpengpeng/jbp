package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ActivityScoreClearingSearchRequest对象", description = "积分活动结算查询对象")
public class ActivityScoreClearingSearchRequest implements Serializable {

    @ApiModelProperty("活动名称")
    private String activityScoreName;

    @ApiModelProperty("用户账号")
    private String account;
}
