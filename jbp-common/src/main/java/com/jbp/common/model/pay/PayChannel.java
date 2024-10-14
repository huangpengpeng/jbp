package com.jbp.common.model.pay;

import com.baomidou.mybatisplus.annotation.TableName;
import com.jbp.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "pay_channel", autoResultMap = true)
@ApiModel(value = "PayChannel对象", description = "支付渠道")
public class PayChannel extends BaseModel {

    @ApiModelProperty(value = "渠道名称 易宝 连连 杉德宝")
    private String name;

    @ApiModelProperty(value = "渠道编码  yop  lianlian  sdb")
    private String code;

    @ApiModelProperty(value = "渠道顶级商编")
    private String parentMerchantNo;

    @ApiModelProperty(value = "品牌头像")
    private String brandUrl;

    @ApiModelProperty(value = "私钥")
    private String priKey;

    @ApiModelProperty(value = "公钥")
    private String pubKey;
}
