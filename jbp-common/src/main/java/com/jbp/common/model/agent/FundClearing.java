package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.google.common.collect.Lists;
import com.jbp.common.model.VersionModel;
import com.jbp.common.mybatis.FundClearingItemListHandler;
import com.jbp.common.mybatis.FundClearingProductListHandler;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_fund_clearing", autoResultMap = true)
@ApiModel(value = "FundClearing对象", description = "佣金发放记录")
public class FundClearing extends VersionModel {

    public FundClearing(Integer uid, String externalNo, String commName, BigDecimal commAmt, UserInfo userInfo,
                        List<FundClearingItem> items, List<FundClearingProduct> productList,
                        String description, String remark) {
        this.uid = uid;
        this.uniqueNo = StringUtils.N_TO_10("C_");
        this.externalNo = externalNo;
        this.commName = commName;
        this.commAmt = commAmt;
        this.sendAmt = commAmt;
        this.userInfo = userInfo;
        this.items = items;
        this.productList = productList;
        this.description = description;
        this.status = Constants.已创建.toString();
        this.remark = remark;
        this.ifRefund = false;
        this.createTime = DateTimeUtils.getNow();
    }

    /**
     * 1.初始化数据就是 已创建
     * 2.待审核   发货已确认后 变为待审核
     * 3.业务员审核通过后  就是  待出款  【用户可见】
     * 4.财务审核通过后 已出款 【用户可见】
     */
    public static enum Constants {
        已创建, 待审核, 待出款, 已出款, 已取消, 已拦截
    }

    public static List<String> interceptStatus() {
        return Lists.newArrayList(Constants.已创建.toString(), Constants.待审核.toString(), Constants.待出款.toString());
    }

    public static List<String> contributeStatus() {
        return Lists.newArrayList(Constants.已创建.toString(), Constants.待审核.toString(), Constants.待出款.toString(),Constants.已出款.toString());
    }

    @ApiModelProperty("得奖用户")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("流水单号")
    @TableField("unique_no")
    private String uniqueNo;

    @ApiModelProperty("外部单号")
    @TableField("external_no")
    private String externalNo;

    @ApiModelProperty("佣金名称")
    @TableField("comm_name")
    private String commName;

    @ApiModelProperty("佣金")
    @TableField("comm_amt")
    private BigDecimal commAmt;

    @ApiModelProperty("实发金额")
    @TableField("send_amt")
    private BigDecimal sendAmt;

    @ApiModelProperty("用户信息")
    @TableField(value = "user_info", typeHandler = JacksonTypeHandler.class)
    private UserInfo userInfo;

    @ApiModelProperty("实际出款明细【根据系统配置 积分钱包占比  手续费  管理费】")
    @TableField(value = "items", typeHandler = FundClearingItemListHandler.class)
    private List<FundClearingItem> items;

    @ApiModelProperty("商品信息")
    @TableField(value = "product_list", typeHandler = FundClearingProductListHandler.class)
    private List<FundClearingProduct> productList;

    @ApiModelProperty("描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("结算时间")
    @TableField("clearing_time")
    private Date clearingTime;

    @ApiModelProperty("结算状态  已创建  待审核  待出款  已出款  已取消  已拦截")
    @TableField("status")
    private String status;

    @ApiModelProperty("是否退回")
    private Boolean ifRefund;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty("得奖用户账户")
    @TableField(exist = false)
    private String account;

    @ApiModelProperty("用户昵称")
    @TableField(exist = false)
    private String nickName;

    @ApiModelProperty("团队名称")
    @TableField(exist = false)
    private String teamName;

    @ApiModelProperty("回退时间")
    @TableField(exist = false)
    private Date returnTime;


}
