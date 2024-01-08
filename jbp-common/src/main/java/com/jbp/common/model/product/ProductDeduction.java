package com.jbp.common.model.product;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
}
