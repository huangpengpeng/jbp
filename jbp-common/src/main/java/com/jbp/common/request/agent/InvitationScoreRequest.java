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
@ApiModel(value = "InvitationScoreRequest对象", description = "销售业绩汇总请求对象")
public class InvitationScoreRequest implements Serializable {
    @ApiModelProperty("账户")
    private String account;
    @ApiModelProperty(value = "付款时间区间")
    private String dateLimit;

}
