package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="LztInfoResponse", description="来账通商户首页数据")
public class LztInfoResponse implements Serializable {

    public LztInfoResponse(Integer accountNum, BigDecimal totalAmt, BigDecimal yesterdayOutAmt,
                           BigDecimal yesterdayInAmt, BigDecimal todayOutAmt, BigDecimal todayInAmt) {
        this.accountNum = accountNum;
        this.totalAmt = totalAmt;
        this.yesterdayOutAmt = yesterdayOutAmt;
        this.yesterdayInAmt = yesterdayInAmt;
        this.todayOutAmt = todayOutAmt;
        this.todayInAmt = todayInAmt;
    }

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "账户个数")
    private Integer accountNum;

    @ApiModelProperty(value = "总余额")
    private BigDecimal totalAmt;

    @ApiModelProperty(value = "昨天出金")
    private BigDecimal yesterdayOutAmt;

    @ApiModelProperty(value = "昨天入金")
    private BigDecimal yesterdayInAmt;

    @ApiModelProperty(value = "今天出金")
    private BigDecimal todayOutAmt;

    @ApiModelProperty(value = "今天入金")
    private BigDecimal todayInAmt;
}
