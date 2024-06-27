package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_fund_clearing_record", autoResultMap = true)
@ApiModel(value = "FundClearingRecord对象", description = "佣金统计")
public class FundClearingRecord extends BaseModel {

    public FundClearingRecord(Integer uid, String account, String nickname, Integer orderUid, String orderAccount, String orderNickname,
                              String externalNo, String commName, BigDecimal commAmt,
                              Integer productId, String productName, BigDecimal price, BigDecimal score, Integer quantity,
                              BigDecimal rewardValue, String rewardType, String description) {
        this.uid = uid;
        this.account = account;
        this.nickname = nickname;
        this.orderUid = orderUid;
        this.orderAccount = orderAccount;
        this.orderNickname = orderNickname;
        this.status = Constants.正常.toString();
        this.uniqueNo = StringUtils.N_TO_10("FCR_");
        this.externalNo = externalNo;
        this.commName = commName;
        this.commAmt = commAmt;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.score = score;
        this.quantity = quantity;
        this.rewardValue = rewardValue;
        this.rewardType = rewardType;
        this.description = description;
        this.createTime = DateTimeUtils.getNow();
        this.time = DateTimeUtils.format(this.createTime, DateTimeUtils.DEFAULT_YYYY_MM_FORMAT_PATTERN);
    }

    /**
     * 1.初始化数据就是 正常
     * 2.退款   订单退款更新
     * 3.取消 后续认为取消
     */
    public static enum Constants {
        正常, 取消
    }

    @ApiModelProperty("得奖用户")
    private Integer uid;

    @ApiModelProperty("得奖账户")
    private String account;

    @ApiModelProperty("得奖昵称")
    private String nickname;

    @ApiModelProperty("下单账户")
    private Integer orderUid;

    @ApiModelProperty("下单账户")
    private String orderAccount;

    @ApiModelProperty("下单昵称")
    private String orderNickname;

    @ApiModelProperty("正常, 取消")
    private String status;

    @ApiModelProperty("流水单号")
    private String uniqueNo;

    @ApiModelProperty("外部单号")
    private String externalNo;

    @ApiModelProperty("佣金名称")
    private String commName;

    @ApiModelProperty("佣金")
    private BigDecimal commAmt;

    @ApiModelProperty("商品ID")
    private Integer productId;

    @ApiModelProperty("商品名称")
    private String productName;

    @ApiModelProperty("总价")
    private BigDecimal price;

    @ApiModelProperty("积分")
    private BigDecimal score;

    @ApiModelProperty("数量")
    private Integer quantity;

    @ApiModelProperty("得奖值")
    private BigDecimal rewardValue;

    @ApiModelProperty("得奖类型")
    private String rewardType;

    @ApiModelProperty("描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("统计时间")
    @TableField("time")
    private String time;

    @ApiModelProperty("下单时间")
    @TableField(exist = false)
    private String payTime;
}
