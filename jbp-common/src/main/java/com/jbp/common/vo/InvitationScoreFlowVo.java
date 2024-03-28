package com.jbp.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "InvitationScoreFlowVo对象", description = "销售业绩明细列表导出模板")
public class InvitationScoreFlowVo {

    @ApiModelProperty("积分")
    private BigDecimal score;

    @ApiModelProperty("方向")
    private String action;

    @ApiModelProperty("操作")
    private String operate;

    @ApiModelProperty("单号")
    private String ordersSn;

    @ApiModelProperty("付款时间")
    private Date payTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("用户账户")
    private String account;

    @ApiModelProperty("下单用户账户")
    private String orderAccount;

    @ApiModelProperty("用户id")
    private Integer uid;
}
