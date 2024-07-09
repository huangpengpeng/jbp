package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LztSalaryTransferRequest对象", description = "薪资代发新增")
public class LztSalaryTransferRequest {

    @ApiModelProperty(value = "户名")
    private String bankAcctName;

    @ApiModelProperty(value = "银行账号")
    private String bankAcctNo;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "金额")
    private BigDecimal amt;

    @ApiModelProperty(value = "月份")
    private String time;

    @ApiModelProperty(value = "银行编码  导入不需要传")
    private String bankCode;
}
