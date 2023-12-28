package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 邀请业绩
 */


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_invitation_score", autoResultMap = true)
@ApiModel(value="InvitationScore对象", description="销售业绩汇总")
public class InvitationScore extends BaseModel {

    public InvitationScore(Integer uid) {
        this.uid = uid;
        this.score = BigDecimal.ZERO;
    }

    @ApiModelProperty("用户id")
    private Integer uid;

    @ApiModelProperty("积分")
    @TableField("score")
    private BigDecimal score;

    @ApiModelProperty("账户")
    @TableField(exist = false)
    private String accountNo;
}
