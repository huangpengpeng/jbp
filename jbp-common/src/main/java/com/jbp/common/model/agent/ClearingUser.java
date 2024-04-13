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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_clearing_user", autoResultMap = true)
@ApiModel(value="ClearingUser对象", description="结算名单")
public class ClearingUser extends BaseModel {

    public ClearingUser(Long clearingId, Integer uid, String accountNo, Long level, String levelName, String rule) {
        this.clearingId = clearingId;
        this.uid = uid;
        this.accountNo = accountNo;
        this.level = level;
        this.levelName = levelName;
        this.rule = rule;
    }

    @ApiModelProperty("结算ID")
    @TableField("clearingId")
    private Long clearingId;

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

    @ApiModelProperty("得奖规则")
    @TableField("rule")
    private String rule;
}
