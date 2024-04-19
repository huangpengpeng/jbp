package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "LztPayChannelRequest对象", description = "支付渠道新增")
public class LztPayChannelRequest {

    @ApiModelProperty(value = "数据ID")
    private Long id;

    @ApiModelProperty(value = "必填 渠道名称 显示给用户")
    private String name;

    @ApiModelProperty(value = "必填 渠道类型 易宝  连连  苏宁")
    private String type;

    @ApiModelProperty(value = "选中商户后台才新增的默认传 商户编号")
    private Integer merId;

    @ApiModelProperty(value = "必填 平台账户ID")
    private String partnerId;

    @ApiModelProperty(value = "非必填 私钥")
    private String priKey;

    @ApiModelProperty(value = "交易模型  0 默认  1 提现免验 2 免验")
    private Integer tradeModel;

    @ApiModelProperty(value = "风控类目")
    private String frmsWareCategory;

    @ApiModelProperty(value = "必填  小数 允许4个 手续费(%)")
    private BigDecimal handlingFee;
}
