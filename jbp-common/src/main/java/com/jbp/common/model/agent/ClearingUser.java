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
@TableName(value = "eb_clearing_user", autoResultMap = true)
@ApiModel(value="ClearingUser对象", description="结算名单")
public class ClearingUser extends BaseModel {

    @ApiModelProperty("结算ID")
    @TableField("clearingId")
    private Long clearingId;

    @ApiModelProperty("得奖用户")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("得奖账户")
    @TableField("accountNo")
    private String accountNo;

    @ApiModelProperty("等级")
    @TableField("capaId")
    private Long capaId;

    @ApiModelProperty("等级名称")
    @TableField("capaName")
    private String capaName;

    @ApiModelProperty("星级")
    @TableField("capaXsId")
    private Long capaXsId;

    @ApiModelProperty("星级名称")
    @TableField("capaXsName")
    private String capaXsName;

    @ApiModelProperty("得奖规则")
    @TableField("rule")
    private String rule;
}
