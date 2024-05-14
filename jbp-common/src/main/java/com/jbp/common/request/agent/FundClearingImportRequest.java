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

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FundClearingImportRequest对象", description = "佣金发放记录导入对象")
public class FundClearingImportRequest implements Serializable {

    @ApiModelProperty(value = "账号",required = true)
    @NotEmpty(message = "账号不能为空")
    private String  account;

    @ApiModelProperty(value = "外部订单号",required = true)
    private String externalNo;

    @ApiModelProperty(value = "佣金名称",required = true)
    @NotEmpty(message = "佣金类型不能为空")
    private String commName;

    @ApiModelProperty(value = "佣金金额",required = true)
    @NotNull(message = "佣金金额不能为空")
    private BigDecimal commAmt;

    @ApiModelProperty(value = "佣金描述",required = true)
    @NotEmpty(message = "佣金描述不能为空")
    private String description;

    @ApiModelProperty(value = "佣金备注",required = true)
    @NotEmpty(message = "佣金备注不能为空")
    private String remark;

}
