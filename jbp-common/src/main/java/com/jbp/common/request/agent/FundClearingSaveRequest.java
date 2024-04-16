package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FundClearingSaveRequest对象", description = "佣金发放记录新增对象")
public class FundClearingSaveRequest implements Serializable {

    @ApiModelProperty("账号")
    @NotEmpty(message = "账号不能为空")
    private String  account;

    @ApiModelProperty("关联订单号")
    private String orderNo;

    @ApiModelProperty("佣金类型")
    @NotEmpty(message = "佣金类型不能为空")
    private String type;

    @ApiModelProperty("佣金金额")
    @NotNull(message = "佣金金额不能为空")
    private BigDecimal clearingFee;

    @ApiModelProperty("佣金描述")
    @NotEmpty(message = "佣金描述不能为空")
    private String description;

    @ApiModelProperty("佣金备注")
    @NotEmpty(message = "佣金备注不能为空")
    private String remark;
}
