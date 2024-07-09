package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_score_download_excel", autoResultMap = true)
@ApiModel(value="ScoreDownloadExcel对象", description="等级积分下载")
public class ScoreDownloadExcel extends BaseModel {


    @ApiModelProperty("名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("账户编号")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("得奖账户")
    @TableField("account")
    private String account;

    @ApiModelProperty("账户昵称")
    @TableField("nickName")
    private String nickName;

    @ApiModelProperty("团队名称")
    @TableField("teamName")
    private String teamName;

    @ApiModelProperty("等级名称")
    @TableField("capaName")
    private String capaName;

    @ApiModelProperty("星级名称")
    @TableField("capaXsName")
    private String capaXsName;

    @ApiModelProperty("订单业绩")
    @TableField("score")
    private BigDecimal score;

    @ApiModelProperty("订单单号")
    @TableField("orderSn")
    private String orderSn;

    @ApiModelProperty("下单产品")
    @TableField("productName")
    private String productName;

    @ApiModelProperty("产品编码")
    @TableField("barCode")
    private String barCode;

    @ApiModelProperty("产品数量")
    @TableField("payNum")
    private Integer payNum;

    @ApiModelProperty("支付金额")
    @TableField("payPrice")
    private BigDecimal payPrice;

    @ApiModelProperty("下单用户")
    @TableField("orderUid")
    private Integer orderUid;

    @ApiModelProperty("下单账户")
    @TableField("orderAccount")
    private String orderAccount;

    @ApiModelProperty("付款时间")
    @TableField("payTime")
    private Date payTime;

    @ApiModelProperty("开始时间")
    @TableField("startTime")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @TableField("endTime")
    private Date endTime;

    @ApiModelProperty("折算后业绩")
    @TableField("score2")
    private BigDecimal score2;
}
