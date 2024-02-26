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
@ApiModel(value = "ChannelIdentityRequest对象", description = "渠道身份信息请求对象")
public class ChannelIdentityPageRequest  implements Serializable {
    @ApiModelProperty("账户名称")
    private String account;

    @ApiModelProperty("身份证号码")
    private String idCardNo;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("渠道名称")
    private String channel;
}
