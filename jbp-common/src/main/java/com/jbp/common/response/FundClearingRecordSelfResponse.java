package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="FundClearingRecordResponse对象", description="佣金统计汇总响应对象")
public class FundClearingRecordSelfResponse implements Serializable {

    @ApiModelProperty("总金额")
    private BigDecimal amount;

    @ApiModelProperty("总金额")
    private List<FundClearingRecordResponse> list;
}
