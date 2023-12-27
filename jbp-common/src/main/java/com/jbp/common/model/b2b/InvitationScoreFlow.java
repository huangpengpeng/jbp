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
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "b2b_invitation_score_flow",autoResultMap = true)
@ApiModel(value="InvitationScoreFlow对象", description="销售业绩明细")
public class InvitationScoreFlow extends BaseModel {

    public InvitationScoreFlow(Integer uid, Integer orderUid, BigDecimal score, String action,
                               String operate, String ordersSn, Date payTime, List<ProductInfoDto> productInfo, String remark) {
        this.uid = uid;
        this.orderUid = orderUid;
        this.score = score;
        this.action = action;
        this.operate = operate;
        this.ordersSn = ordersSn;
        this.payTime = payTime;
        this.productInfo = productInfo;
        this.remark = remark;
    }

    @ApiModelProperty("用户id")
    private Integer uid;

    @ApiModelProperty("下单用户id")
    @TableField("orderUid")
    private Integer orderUid;

    @ApiModelProperty("积分")
    @TableField("score")
    private BigDecimal score;

    @ApiModelProperty("方向")
    @TableField("action")
    private String action;

    @ApiModelProperty("操作")
    @TableField("operate")
    private String operate;

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

    @ApiModelProperty("用户账户")
    @TableField(exist = false)
    private String accountNo;

    @ApiModelProperty("下单用户账户")
    @TableField(exist = false)
    private String orderAccountNo;
}
