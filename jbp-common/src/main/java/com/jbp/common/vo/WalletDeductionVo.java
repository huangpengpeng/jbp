package com.jbp.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WalletDeductionVo对象", description="钱包积分抵扣")
public class WalletDeductionVo {

    @ApiModelProperty(value = "钱包类型")
    private Integer type;

    @ApiModelProperty(value = "抵扣金额")
    private BigDecimal deductionFee;

    @ApiModelProperty(value = "总pv")
    private BigDecimal pv;
}
