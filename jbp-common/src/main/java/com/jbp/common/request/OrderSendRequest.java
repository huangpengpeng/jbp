package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;

import com.jbp.common.annotation.StringContains;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 订单发货对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "OrderSendRequest对象", description = "订单发货对象")
public class OrderSendRequest {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单编号", required = true)
    @NotBlank(message = "订单编号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "发货类型：express-快递，fictitious：虚拟发货", allowableValues = "range[express,fictitious]", required = true)
    @NotBlank(message = "请选择发货类型")
    @StringContains(limitValues = {"express","fictitious"}, message = "未知的发货类型")
    private String deliveryType;

    @ApiModelProperty(value = "快递公司编码")
    @NotBlank(message = "快递公司编码不能为空")
    private String expressCode;

    @ApiModelProperty(value = "快递单号")
//    @NotBlank(message = "快递单号不能为空")
    private String expressNumber;

    @ApiModelProperty(value = "发货记录类型，1正常、2电子面单")
    private Integer expressRecordType;

    @ApiModelProperty(value = "电子面单模板,电子面单必传")
    private String expressTempId;

    @ApiModelProperty(value = "寄件人姓名,电子面单必传")
    private String toName;

    @ApiModelProperty(value = "寄件人电话,电子面单必传")
    private String toTel;

    @ApiModelProperty(value = "寄件人地址,电子面单必传")
    private String toAddr;

    @ApiModelProperty(value = "是否拆单发货", required = true)
    @NotNull(message = "是否拆单发货不能为空")
    private Boolean isSplit;

    @ApiModelProperty(value = "拆单发货详情列表,拆单发货时必传")
    @Valid
    private List<SplitOrderSendDetailRequest> detailList;
}
