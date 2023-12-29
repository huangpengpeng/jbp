package com.jbp.common.model.agent;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.jbp.common.model.VersionModel;
import com.jbp.common.utils.CrmebUtil;
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
@TableName(value = "eb_clearing_bonus", autoResultMap = true)
@ApiModel(value="ClearingBonus对象", description="业绩结算奖金记录")
public class ClearingBonus extends VersionModel {

    public static enum Constants {
        待审核, 已审核
    }


    @ApiModelProperty("得奖用户")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("得奖账户")
    @TableField("accountNo")
    private String accountNo;

    @ApiModelProperty("批次号")
    @TableField("batchNo")
    private String batchNo;

    @ApiModelProperty("流水单号")
    @TableField("uniqueNo")
    private String uniqueNo;

    @ApiModelProperty("佣金名称")
    @TableField("commName")
    private String commName;

    @ApiModelProperty("佣金")
    @TableField("commAmt")
    private BigDecimal commAmt;

    @ApiModelProperty("实发金额")
    @TableField("sendAmt")
    private BigDecimal sendAmt;

    @ApiModelProperty("用户信息")
    @TableField(value = "userInfo", typeHandler = JacksonTypeHandler.class)
    private UserInfo userInfo;

    @ApiModelProperty("描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("结算时间")
    @TableField("clearTime")
    private Date clearTime;

    @ApiModelProperty("结算状态 待审核  已审核")
    @TableField("status")
    private String status;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("创建时间")
    @TableField("createTime")
    private Date createTime;
}
