package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 市场等级
 */

@Data
public class CapaRequest {

    @ApiModelProperty(value = "主键")
    private Integer id;

    @ApiModelProperty("等级名字")
    @NotEmpty(message = "等级名字名称不能为空")
    private String name;

    @ApiModelProperty("下一个级别")
    private Long pCapaId;

    @ApiModelProperty("数字等级")
    @NotNull(message = "数字等级不能为空")
    private Integer rankNum;

    @ApiModelProperty("等级图标地址")
    @NotEmpty(message = "等级图标地址不能为空")
    private String iconUrl;

    @ApiModelProperty("升级提醒图片")
    @NotEmpty(message = "升级提醒图片不能为空")
    private String riseImgUrl;

    @ApiModelProperty("邀请图片")
    @NotEmpty(message = "邀请图片不能为空")
    private String shareImgUrl;

    @ApiModelProperty("计算表达式")
    private String parser;


    @ApiModelProperty(value = "是否有供货权")
    private Boolean ifSupply;

    @ApiModelProperty(value = "是否向公司订货")
    private Boolean ifCompany;

    @ApiModelProperty(value = "订货规则")
    private String orderRule;

    @ApiModelProperty(value = "订货金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "补货金额")
    private BigDecimal repAmount;


}
