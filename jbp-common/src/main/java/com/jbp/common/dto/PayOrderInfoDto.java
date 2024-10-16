package com.jbp.common.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="PayOrderInfoDto对象", description="付款订单信息")
public class PayOrderInfoDto implements Serializable {

    private String goodsName;

    private Integer goodsQuantity;

    private BigDecimal goodsPrice;
}
