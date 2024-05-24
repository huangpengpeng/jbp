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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "eb_lzt_pay_channel", autoResultMap = true)
@ApiModel(value="LztPayChannel对象", description="来账通支付信息")
@NoArgsConstructor
public class LztPayChannel extends BaseModel {

    public LztPayChannel(String name, String type, Integer mer_id, String partnerId,
                         String priKey, Integer tradeModel, String frmsWareCategory,
                         BigDecimal handlingFee) {
        this.name = name;
        this.type = type;
        this.merId = mer_id;
        this.partnerId = partnerId;
        this.priKey = priKey;
        this.tradeModel = tradeModel;
        this.frmsWareCategory = frmsWareCategory;
        this.handlingFee = handlingFee;
    }

    @ApiModelProperty(value = "渠道名称 显示给用户")
    private String name;

    @ApiModelProperty(value = "渠道类型 易宝  连连  苏宁")
    private String type;

    @ApiModelProperty(value = "商户编号")
    private Integer merId;

    @ApiModelProperty(value = "平台账户ID")
    private String partnerId;

    @ApiModelProperty(value = "私钥")
    private String priKey;

    @ApiModelProperty(value = "交易模型  0 转账（密码+验证码）+提现（密码+验证码）  1 转账（无密码+无验证码） 提现（验证码） 2 转账（无密码+无验证码）提现（无密码+无验证码）")
    private Integer tradeModel;

    @ApiModelProperty(value = "风控类目")
    private String frmsWareCategory;

    @ApiModelProperty(value = "手续费(%)")
    private BigDecimal handlingFee;

    @ApiModelProperty(value = "商户名称")
    @TableField(exist = false)
    private String merName;
}
