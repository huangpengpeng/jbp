package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RelationScoreEditRequest对象", description = "服务业绩汇总编辑对象")
public class RelationScoreEditRequest implements Serializable {
    @ApiModelProperty("编号")
    private Long id;
    @ApiModelProperty("可用积分")
    private BigDecimal usableScore;

    @ApiModelProperty("对碰积分")
    private BigDecimal usedScore;

    @ApiModelProperty("点位")
    @TableField("node")
    private int node;
}
