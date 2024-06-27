package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FundClearingRecordTotalRequest对象", description = "佣金统计汇总请求对象")
public class FundClearingRecordTotalRequest {
    @ApiModelProperty("下单账号/昵称")
    private String orderAccount;

    @ApiModelProperty("获得者账号/昵称")
    private String account;

    @ApiModelProperty("月份")
    private String month;
}
