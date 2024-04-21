package com.jbp.common.model.agent;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import com.jbp.common.model.VersionModel;
import com.jbp.common.utils.DateTimeUtils;
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
@ApiModel(value="ClearingBonusFlow对象", description="结算奖金明细")
public class ClearingBonusFlow extends BaseModel {

    public ClearingBonusFlow(Integer uid, String accountNo, Long level, String levelName,
                             Long clearingId, String name, String commName,
                             BigDecimal commAmt, String postscript, String rule) {
        this.uid = uid;
        this.accountNo = accountNo;
        this.level = level;
        this.levelName = levelName;
        this.clearingId = clearingId;
        this.name = name;
        this.commName = commName;
        this.commAmt = commAmt;
        this.postscript = postscript;
        this.rule = rule;
        this.createTime = DateTimeUtils.getNow();
    }

    @ApiModelProperty("得奖用户")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("得奖账户")
    @TableField("accountNo")
    private String accountNo;

    @ApiModelProperty("级别")
    @TableField("level")
    private Long level;

    @ApiModelProperty("级别名称")
    @TableField("levelName")
    private String levelName;

    @ApiModelProperty("结算ID")
    @TableField("clearingId")
    private Long clearingId;

    @ApiModelProperty("结算名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("佣金名称")
    @TableField("commName")
    private String commName;

    @ApiModelProperty("佣金")
    @TableField("commAmt")
    private BigDecimal commAmt;

    @ApiModelProperty("凭证")
    @TableField("postscript")
    private String postscript;

    @ApiModelProperty("得奖规则")
    @TableField("rule")
    private String rule;

    @ApiModelProperty("创建时间")
    @TableField("createTime")
    private Date createTime;
}
