package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FundClearingRecordRequest对象", description = "佣金统计请求对象")
public class FundClearingRecordSelfRequest {

    @ApiModelProperty("时间:YYYY-MM")
    private String day;
}
