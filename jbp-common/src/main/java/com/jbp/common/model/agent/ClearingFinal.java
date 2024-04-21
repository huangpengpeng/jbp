package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_clearing_final", autoResultMap = true)
@ApiModel(value="ClearingFinal对象", description="结算信息")
public class ClearingFinal extends BaseModel {
    public static enum Constants {
        待结算, 待出款, 已出款
    }

    @ApiModelProperty("结算名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("佣金类型")
    @TableField("commType")
    private Integer commType;

    @ApiModelProperty("佣金名称")
    @TableField("commName")
    private String commName;

    @ApiModelProperty("总业绩")
    @TableField("totalScore")
    private BigDecimal totalScore;

    @ApiModelProperty("总金额")
    @TableField("totalAmt")
    private BigDecimal totalAmt;

    @ApiModelProperty("开始时间")
    @TableField("startTime")
    private String startTime;

    @ApiModelProperty("结束时间")
    @TableField("endTime")
    private String endTime;

    @ApiModelProperty("结算状态 待审核  已审核")
    @TableField("status")
    private String status;

    @ApiModelProperty("结算日志")
    @TableField("logs")
    private String logs;
}
