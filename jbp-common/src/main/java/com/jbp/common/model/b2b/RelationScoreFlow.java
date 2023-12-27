package com.jbp.common.model.b2b;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.dto.ProductInfoDto;
import com.jbp.common.model.BaseModel;
import com.jbp.common.mybatis.ProductInfoListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 点位业绩明细
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "b2b_relation_score_flow", autoResultMap = true)
@ApiModel(value="RelationScoreFlow对象", description="服务业绩明细")
public class RelationScoreFlow extends BaseModel {

    public RelationScoreFlow(Integer uid, Integer orderUid, BigDecimal score, int node,
                             String operate, String action, String ordersSn,
                             Date payTime, List<ProductInfoDto> productInfo, String remark, Integer level, BigDecimal amt, BigDecimal ratio) {
        this.uid = uid;
        this.orderUid = orderUid;
        this.score = score;
        this.node = node;
        this.operate = operate;
        this.action = action;
        this.ordersSn = ordersSn;
        this.payTime = payTime;
        this.productInfo = productInfo;
        this.remark = remark;
        this.level = level;
        this.amt = amt;
        this.ratio = ratio;
    }

    @ApiModelProperty("用户id")
    private Integer uid;

    @ApiModelProperty("下单用户id")
    @TableField("orderUid")
    private Integer orderUid;

    @ApiModelProperty("积分")
    @TableField("score")
    private BigDecimal score;

    @ApiModelProperty("点位")
    @TableField("node")
    private int node;

    @ApiModelProperty("操作")
    @TableField("operate")
    private String operate;

    @ApiModelProperty("方向")
    @TableField("action")
    private String action;

    @ApiModelProperty("单号")
    @TableField("ordersSn")
    private String ordersSn;

    @ApiModelProperty("付款时间")
    @TableField("payTime")
    private Date payTime;

    @ApiModelProperty("商品信息")
    @TableField(value = "productInfo", typeHandler = ProductInfoListHandler.class)
    private List<ProductInfoDto> productInfo;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("层数")
    @TableField("level")
    private Integer level;

    @ApiModelProperty("奖金")
    @TableField("amt")
    private BigDecimal amt;

    @ApiModelProperty("比例")
    @TableField("ratio")
    private BigDecimal ratio;


    @ApiModelProperty("用户账户")
    @TableField(exist = false)
    private String accountNo;

    @ApiModelProperty("下单用户账户")
    @TableField(exist = false)
    private String orderAccountNo;
}
