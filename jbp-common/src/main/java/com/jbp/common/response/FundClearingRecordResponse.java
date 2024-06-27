package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="FundClearingRecordResponse对象", description="佣金统计汇总响应对象")
public class FundClearingRecordResponse implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("获得者账号")
    private String account;

    @ApiModelProperty("得奖昵称")
    private String nickname;

    @ApiModelProperty("月份:YYYY年MM月")
    private String month;

    @ApiModelProperty("金额")
    private BigDecimal amount;
}
