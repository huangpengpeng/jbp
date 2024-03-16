package com.jbp.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.mybatis.ProductInfoListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RelationScoreFlowVo对象", description = "服务业绩明细列表导出模板")
public class RelationScoreFlowVo {

    @ApiModelProperty("积分")
    private BigDecimal score;

    @ApiModelProperty("点位")
    private int node;

    @ApiModelProperty("操作")
    private String operate;

    @ApiModelProperty("方向")
    private String action;

    @ApiModelProperty("单号")
    private String ordersSn;

    @ApiModelProperty("付款时间")
    private Date payTime;

    @ApiModelProperty("奖金")
    private BigDecimal amt;

    @ApiModelProperty("层数")
    private Integer level;

    @ApiModelProperty("层级比例")
    private BigDecimal levelRatio;

    @ApiModelProperty("比例")
    private BigDecimal ratio;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("用户账户")
    private String account;

    @ApiModelProperty("下单用户账户")
    private String orderAccount;
}
