package com.jbp.common.vo;

import com.jbp.common.annotation.StringContains;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单Excel VO对象
 * @Author 莫名
 * @Date 2023/6/28 12:24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="OrderShippingExcelVo", description = "订单发货Excel VO对象")
public class OrderShippingExcelVo implements Serializable {

    private static final long serialVersionUID = -8330957183745338822L;

    @ApiModelProperty(value = "订单号")
    @NotBlank(message = "订单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "发货类型 express-快递，fictitious：虚拟发货")
    @NotBlank(message = "发货类型不能为空")
    @StringContains(limitValues = {"快递","虚拟发货"}, message = "未知的发货类型")
    private String deliveryTypeName;

    @ApiModelProperty(value = "快递公司")
    @NotBlank(message = "快递公司不能为空")
    private String expressName;

    @ApiModelProperty(value = "快递单号")
    @NotBlank(message = "快递单号不能为空")
    private String expressNumber;

    @ApiModelProperty(value = "订单详情id")
    @NotNull(message = "订单详情id不能为空")
    private Integer orderDetailId;

    @ApiModelProperty(value = "发货数量")
    @NotNull(message = "发货数量不能为空")
    private Integer num;
}
