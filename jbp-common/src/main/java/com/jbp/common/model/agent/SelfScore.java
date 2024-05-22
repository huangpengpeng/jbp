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
@TableName(value = "eb_self_score", autoResultMap = true)
@ApiModel(value="SelfScore对象", description="个人业绩汇总")
public class SelfScore extends BaseModel {

    public SelfScore(Integer uid) {
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
    private String account;

    @ApiModelProperty("个人积分")
    @TableField(exist = false)
    private BigDecimal selfScore;

    @ApiModelProperty("用户昵称")
    @TableField(exist = false)
    private String nickname;

}
