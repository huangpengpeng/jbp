package com.jbp.common.model.agent;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 出款备注
 */
@Data
@NoArgsConstructor
public class FundClearingItem implements Serializable {

    public FundClearingItem(String name, Integer walletType, BigDecimal amt) {
        this.name = name;
        this.walletType = walletType;
        this.amt = amt;
    }

    @ApiModelProperty("出款名称")
    private String name;

    @ApiModelProperty("钱包类型")
    private Integer walletType;

    @ApiModelProperty("出款金额")
    private BigDecimal amt;
}
