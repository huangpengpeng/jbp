package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 出款备注
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
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

    @ApiModelProperty("钱包名称")
    @TableField(exist = false)
    private String walletName;
}
