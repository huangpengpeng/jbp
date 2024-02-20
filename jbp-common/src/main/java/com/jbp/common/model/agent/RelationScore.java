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
@TableName(value = "eb_relation_score", autoResultMap = true)
@ApiModel(value="RelationScore对象", description="服务业绩汇总")
public class RelationScore extends BaseModel {

    public RelationScore(Integer uid, int node) {
        this.uid = uid;
        this.usableScore = BigDecimal.ZERO;
        this.usedScore = BigDecimal.ZERO;
        this.node = node;
    }

    @ApiModelProperty("用户id")
    private Integer uid;

    @ApiModelProperty("可用积分")
    private BigDecimal usableScore;

    @ApiModelProperty("对碰积分")
    private BigDecimal usedScore;

    @ApiModelProperty("点位")
    @TableField("node")
    private int node;

    @ApiModelProperty("账户")
    @TableField(exist = false)
    private String account;

}
