package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_capa_order")
@ApiModel(value = "CapaOrder对象", description = "等级订货表")
public class CapaOrder extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "等级id")
    private Long capaId;

    @ApiModelProperty(value = "是否有供货权")
    private Boolean ifSupply;

    @ApiModelProperty(value = "是否向公司订货")
    private Boolean ifCompany;

    @ApiModelProperty(value = "订货金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "补货金额")
    private BigDecimal repAmount;

    @ApiModelProperty(value = "升级图片是否展示")
    private Boolean ifShow;

    @ApiModelProperty(value = "分值")
    private Integer score;

    @ApiModelProperty(value = "等级名称")
    @TableField(exist = false)
    private String capaName;

}
