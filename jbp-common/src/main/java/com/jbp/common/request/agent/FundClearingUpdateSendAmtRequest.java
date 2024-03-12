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
@ApiModel(value = "FundClearingUpdateSendAmtRequest对象", description = "佣金发放记录发放金额修改对象")
public class FundClearingUpdateSendAmtRequest implements Serializable {
    @ApiModelProperty("编号")
    @NotNull(message = "编号不能为空")
    private Long id;

    @ApiModelProperty("金额")
    @NotNull(message = "金额不能为空")
    private BigDecimal sendAmt;

    @ApiModelProperty("备注")
    private String remark;

}
