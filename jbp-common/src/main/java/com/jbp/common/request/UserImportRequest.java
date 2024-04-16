package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserImportRequest", description = "用户导入")
public class UserImportRequest implements Serializable {

    @ApiModelProperty(value = "账户", required = true)
    @NotBlank(message = "账户不能为空")
    private String account;

    @ApiModelProperty(value = "昵称", required = true)
    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @ApiModelProperty(value = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @ApiModelProperty(value = "销售人", required = true)
    @NotBlank(message = "销售人账号不能为空")
    private String paccount;

    @ApiModelProperty(value = "服务人", required = true)
    private String raccount;

    @ApiModelProperty(value = "服务人节点", required = true)
    private Integer node;

    @ApiModelProperty(value = "登记编号", required = true)
    @NotNull(message = "等级编号不能为空")
    private Long capaId;

    @ApiModelProperty(value = "星级编号", required = true)
    private Long capaXsId;

    @ApiModelProperty(value = "是否开店【是  否】", required = true)
    @NotBlank(message = "是否开店不能为空")
    private String openShop;

    @ApiModelProperty(value = "可用业绩", required = true)
    @NotNull(message = "可用业绩不能为空")
    private BigDecimal usableScore;

    @ApiModelProperty(value = "已用业绩", required = true)
    @NotNull(message = "已用业绩不能为空")
    private BigDecimal usedScore;

    @ApiModelProperty(value = "购物积分", required = true)
    @NotNull(message = "购物积分不能为空")
    private BigDecimal gouWu;

    @ApiModelProperty(value = "奖励积分", required = true)
    @NotNull(message = "奖励积分不能为空")
    private BigDecimal jiangLi;

    @ApiModelProperty(value = "换购积分", required = true)
    @NotNull(message = "换购积分不能为空")
    private BigDecimal huangGou;

    @ApiModelProperty(value = "福券积分", required = true)
    @NotNull(message = "福券积分不能为空")
    private BigDecimal fuQuan;
}
