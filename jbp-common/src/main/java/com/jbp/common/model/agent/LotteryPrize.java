package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Maps;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.RiseConditionListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 市场等级
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lottery_prize", autoResultMap = true)
@ApiModel(value="LotteryPrize对象", description="抽奖奖品")
public class LotteryPrize extends BaseModel {


    @ApiModelProperty("活动ID")
    @TableField("lottery_id")
    private Long lotteryId;

    @ApiModelProperty("奖品名称")
    @TableField("prize_name")
    private String prizeName;

    @ApiModelProperty("奖品类型， -1-谢谢参与、1-普通奖品")
    @TableField("prize_type")
    private Integer prizeType;

    @ApiModelProperty("总库存")
    @TableField("total_stock")
    private  Integer totalStock;

    @ApiModelProperty("可用库存")
    @TableField("valid_stock")
    private Integer validStock;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("图片")
    @TableField("images")
    private String images;


    @ApiModelProperty("奖项概率")
    @TableField(exist = false)
    private BigDecimal percent;

    @ApiModelProperty("默认奖项")
    @TableField(exist = false)
    private Integer defaultItem;

    @ApiModelProperty("奖项id")
    @TableField(exist = false)
    private Integer itemId;

    @ApiModelProperty("权重")
    @TableField(exist = false)
    private Integer weight;

    @ApiModelProperty("奖品id")
    @TableField(exist = false)
    private Integer prizeId;

    @ApiModelProperty("序号")
    @TableField(exist = false)
    private Integer number;




}
