package com.jbp.common.model.agent;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.VersionModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 结算奖金
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_clearing_bonus_flow", autoResultMap = true)
@ApiModel(value="ClearingBonusFlow对象", description="业绩结算奖金明细")
public class ClearingBonusFlow extends VersionModel {

    @ApiModelProperty("得奖用户")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("得奖账户")
    @TableField("accountNo")
    private String accountNo;

    @ApiModelProperty("等级")
    @TableField("capaId")
    private Long capaId;

    @ApiModelProperty("星级")
    @TableField("capaXsId")
    private Long capaXsId;

    @ApiModelProperty("批次号")
    @TableField("batchNo")
    private String batchNo;

    @ApiModelProperty("佣金名称")
    @TableField("commName")
    private String commName;

    @ApiModelProperty("佣金")
    @TableField("commAmt")
    private BigDecimal commAmt;

    @ApiModelProperty("描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("创建时间")
    @TableField("createTime")
    private Date createTime;
}
