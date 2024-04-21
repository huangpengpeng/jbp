package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_clearing_vip_user", autoResultMap = true)
@ApiModel(value="ClearingVipUser对象", description="结算VIP名单特殊用户")
public class ClearingVipUser extends BaseModel {

    public ClearingVipUser(Integer uid, String accountNo, Long level, String levelName, Integer commType, String commName,
                           BigDecimal maxAmount, String rule, String description) {
        this.uid = uid;
        this.accountNo = accountNo;
        this.commType = commType;
        this.commName = commName;
        this.level = level;
        this.levelName = levelName;
        this.usedAmount = BigDecimal.ZERO;
        this.maxAmount = maxAmount;
        this.rule = rule;
        this.status = 0;
        this.description = description;
    }

    @ApiModelProperty("用户")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("账户")
    @TableField("accountNo")
    private String accountNo;

    @ApiModelProperty("佣金类型")
    @TableField("commType")
    private Integer commType;

    @ApiModelProperty("佣金名称")
    @TableField("commName")
    private String commName;

    @ApiModelProperty("级别")
    @TableField("level")
    private Long level;

    @ApiModelProperty("级别名称")
    @TableField("levelName")
    private String levelName;

    @ApiModelProperty("已经得奖金额")
    @TableField("usedAmount")
    private BigDecimal usedAmount;

    @ApiModelProperty("最大金额")
    @TableField("maxAmount")
    private BigDecimal maxAmount;

    @ApiModelProperty("得奖规则 比例  上限")
    @TableField("rule")
    private String rule;

    @ApiModelProperty("状态 0有效 -1失效")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("描述")
    @TableField("description")
    private String description;
}
