package com.jbp.common.model.product;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 产品抵扣
 */
@Data
public class ProductDeduction implements Serializable {

    @ApiModelProperty(value = "积分名称")
    private String walletName;

    @ApiModelProperty(value = "积分类型")
    private Integer walletType;

    @ApiModelProperty(value = "抵扣比例")
    private BigDecimal scale;

    @ApiModelProperty(value = "是否计算业绩")
    private Boolean hasPv;

    @ApiModelProperty(value = "抵扣金额")
    @TableField(exist = false)
    private BigDecimal deductionFee;

    @ApiModelProperty(value = "PV金额")
    @TableField(exist = false)
    private BigDecimal pvFee;

}
