package com.jbp.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FundClearingVo对象", description = "佣金发放记录信息")
public class FundClearingVo implements Serializable {

    @ApiModelProperty("流水单号")
    private String uniqueNo;

    @ApiModelProperty("外部单号")
    private String externalNo;

    @ApiModelProperty("佣金名称")
    private String commName;

    @ApiModelProperty("佣金")
    private BigDecimal commAmt;

    @ApiModelProperty("实发金额")
    private BigDecimal sendAmt;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("结算时间")
    private Date clearingTime;

    @ApiModelProperty("结算状态")
    private String status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("得奖用户账户")
    private String account;
}