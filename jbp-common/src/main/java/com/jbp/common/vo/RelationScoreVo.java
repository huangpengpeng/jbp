package com.jbp.common.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ApiModel(value = "RelationScoreVo对象", description = "服务业绩汇总Vo")
public class RelationScoreVo {

    @ApiModelProperty("可用积分")
    private BigDecimal usableScore;

    @ApiModelProperty("对碰积分")
    private BigDecimal usedScore;

    @ApiModelProperty("虚假业绩")
    private BigDecimal fakeScore;

    @ApiModelProperty("点位")
    private int node;

    @ApiModelProperty("账户")
    private String account;

    @ApiModelProperty("创建时间")
    private Date gmtCreated;
}
