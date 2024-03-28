package com.jbp.common.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.jbp.common.model.agent.FundClearingItem;
import com.jbp.common.model.agent.FundClearingProduct;
import com.jbp.common.model.agent.UserInfo;
import com.jbp.common.mybatis.FundClearingItemListHandler;
import com.jbp.common.mybatis.FundClearingProductListHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FundClearingVo对象", description = "佣金发放记录信息")
public class FundClearingVo implements Serializable {
    @ApiModelProperty("编号")
    private long id;

    @ApiModelProperty("流水单号")
    private String uniqueNo;

    @ApiModelProperty("外部单号")
    private String externalNo;

    @ApiModelProperty("佣金名称")
    private String commName;

    @ApiModelProperty("佣金")
    private BigDecimal commAmt;

    @ApiModelProperty("实发金额")
    private BigDecimal sendAmt;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("结算时间")
    private Date clearingTime;

    @ApiModelProperty("结算状态")
    private String status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("得奖用户账户")
    private String account;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("身份证")
    private String idCardNo;

    @ApiModelProperty("银行卡预留手机号")
    private String phone;

    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("卡号")
    private String bankCode;

    @ApiModelProperty("是否退款")
    private Boolean ifRefund;





    @ApiModelProperty("得奖用户")
    @TableField("uid")
    private Integer uid;

    @ApiModelProperty("用户信息")
    @TableField(value = "user_info", typeHandler = JacksonTypeHandler.class)
    private UserInfo userInfo;

    @ApiModelProperty("实际出款明细【根据系统配置 积分钱包占比  手续费  管理费】")
    @TableField(value = "items", typeHandler = FundClearingItemListHandler.class)
    private List<FundClearingItem> items;

    @ApiModelProperty("商品信息")
    @TableField(value = "product_list", typeHandler = FundClearingProductListHandler.class)
    private List<FundClearingProduct> productList;

    @ApiModelProperty("用户昵称")
    @TableField(exist = false)
    private String nickName;

    @ApiModelProperty("团队名称")
    @TableField(exist = false)
    private String teamName;

}
