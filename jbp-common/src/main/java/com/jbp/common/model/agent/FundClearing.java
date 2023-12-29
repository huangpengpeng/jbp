package com.jbp.common.model.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.google.common.collect.Lists;
import com.jbp.common.model.VersionModel;
import com.jbp.common.mybatis.FundClearingItemListHandler;
import com.jbp.common.mybatis.FundClearingProductListHandler;
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
@ApiModel(value="FundClearing对象", description="佣金发放记录")
public class FundClearing extends VersionModel {

    /**
     * 1.初始化数据就是 已创建
     * 2.待审核   发货已确认后 变为待审核
     * 3.业务员审核通过后  就是  待出款  【用户可见】
     * 4.财务审核通过后 已出款 【用户可见】
     */
    public static enum Constants {
        已创建,  待审核,  待出款,  已出款,  已取消,  已拦截
    }

    public static List<String> interceptStatus(){
        return Lists.newArrayList(Constants.已创建.toString(), Constants.待审核.toString(), Constants.待出款.toString());
    }

    @ApiModelProperty("得奖用户")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("流水单号")
    @TableField("uniqueNo")
    private String uniqueNo;

    @ApiModelProperty("外部单号")
    @TableField("externalNo")
    private String externalNo;

    @ApiModelProperty("佣金名称")
    @TableField("commName")
    private String commName;

    @ApiModelProperty("佣金")
    @TableField("commAmt")
    private BigDecimal commAmt;

    @ApiModelProperty("实发金额")
    @TableField("sendAmt")
    private BigDecimal sendAmt;

    @ApiModelProperty("用户信息")
    @TableField(value = "userInfo", typeHandler = JacksonTypeHandler.class)
    private UserInfo userInfo;

    @ApiModelProperty("实际出款明细【根据系统配置 积分钱包占比  手续费  管理费】")
    @TableField(value = "items", typeHandler = FundClearingItemListHandler.class)
    private List<FundClearingItem> items;

    @ApiModelProperty("商品信息")
    @TableField(value = "productList", typeHandler = FundClearingProductListHandler.class)
    private List<FundClearingProduct> productList;

    @ApiModelProperty("描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("结算时间")
    @TableField("clearingTime")
    private Date clearingTime;

    @ApiModelProperty("结算状态  已创建  待审核  待出款  已出款  已取消  已拦截")
    @TableField("status")
    private String status;

    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    @ApiModelProperty("创建时间")
    @TableField("createTime")
    private Date createTime;
}
