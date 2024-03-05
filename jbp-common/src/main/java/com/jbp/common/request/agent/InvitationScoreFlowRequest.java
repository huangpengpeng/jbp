package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "InvitationScoreFlowRequest对象", description = "销售业绩明细请求对象")
public class InvitationScoreFlowRequest implements Serializable {

    @ApiModelProperty("用户账户")
    private String account;

    @ApiModelProperty("下单用户账户")
    private String orderAccount;

    @ApiModelProperty("方向")
    private String action;


    @ApiModelProperty("单号")
    private String ordersSn;
}
