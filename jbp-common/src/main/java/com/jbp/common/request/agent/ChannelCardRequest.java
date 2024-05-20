package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ChannelCardRequest对象", description = "渠道银行卡请求对象")
public class ChannelCardRequest implements Serializable {

    @ApiModelProperty("账户名称")
    private String account;

    @ApiModelProperty("银行卡号")
    private String bankCardNo;

    @ApiModelProperty("银行卡类型")
    private String type;

    @ApiModelProperty("银行卡预留手机号")
    private String phone;

    @ApiModelProperty("团队id")
    private String teamId;
}
