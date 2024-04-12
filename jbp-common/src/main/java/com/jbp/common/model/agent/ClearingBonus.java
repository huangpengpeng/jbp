package com.jbp.common.model.agent;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import com.jbp.common.model.VersionModel;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
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
@TableName(value = "eb_clearing_bonus", autoResultMap = true)
@ApiModel(value="ClearingBonus对象", description="结算奖金")
public class ClearingBonus extends BaseModel {

    public ClearingBonus(Integer uid, String accountNo, Long level, String levelName,
                         Long clearingId, String name, String commName, String uniqueNo, BigDecimal commAmt) {
        this.uid = uid;
        this.accountNo = accountNo;
        this.level = level;
        this.levelName = levelName;
        this.clearingId = clearingId;
        this.name = name;
        this.commName = commName;
        this.uniqueNo = uniqueNo;
        this.commAmt = commAmt;
        this.status = Constants.待出款.name();
        this.createTime = DateTimeUtils.getNow();

    }

    public static enum Constants {
        待出款, 已出款, 已取消
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

    @ApiModelProperty("流水单号")
    @TableField("uniqueNo")
    private String uniqueNo;

    @ApiModelProperty("佣金")
    @TableField("commAmt")
    private BigDecimal commAmt;

    @ApiModelProperty("结算状态 已结算  已出款")
    @TableField("status")
    private String status;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("创建时间")
    @TableField("createTime")
    private Date createTime;

    @ApiModelProperty("结算时间")
    @TableField("clearTime")
    private Date clearTime;
}
