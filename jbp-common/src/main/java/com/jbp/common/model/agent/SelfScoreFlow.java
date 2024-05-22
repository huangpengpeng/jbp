package com.jbp.common.model.agent;

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


@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_self_score_flow", autoResultMap = true)
@ApiModel(value="SelfScoreFlow对象", description="个人业绩明细")
public class SelfScoreFlow extends BaseModel {

    public SelfScoreFlow(Integer uid, BigDecimal score, String action, String operate,
                         String ordersSn, Date payTime,  List<ProductInfoDto> productInfo, String remark) {
        this.uid = uid;
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

    @ApiModelProperty("积分")
    @TableField("score")
    private BigDecimal score;

    @ApiModelProperty("方向  增加|减少")
    @TableField("action")
    private String action;

    @ApiModelProperty("操作  下单|人工")
    @TableField("operate")
    private String operate;

    @ApiModelProperty("单号")
    @TableField("orders_sn")
    private String ordersSn;

    @ApiModelProperty("付款时间")
    @TableField("pay_time")
    private Date payTime;

    @ApiModelProperty("商品信息")
    @TableField(value = "product_info", typeHandler = ProductInfoListHandler.class)
    private List<ProductInfoDto> productInfo;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("账户")
    @TableField(exist = false)
    private String account;

    @ApiModelProperty("用户昵称")
    @TableField(exist = false)
    private String nickname;
}
