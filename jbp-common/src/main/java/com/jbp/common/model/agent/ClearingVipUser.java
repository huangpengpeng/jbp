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

    @ApiModelProperty("用户")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("账户")
    @TableField("accountNo")
    private String accountNo;

    @ApiModelProperty("级别")
    @TableField("level")
    private Long level;

    @ApiModelProperty("级别名称")
    @TableField("levelName")
    private String levelName;

    @ApiModelProperty("已经得奖金额")
    @TableField("usedAmount")
    private BigDecimal usedAmount;

    @ApiModelProperty("得奖规则 比例  上限")
    @TableField("rule")
    private String rule;

    @ApiModelProperty("状态 0有效 -1失效")
    @TableField("status")
    private Integer status;
}
