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
@ApiModel(value = "RelationScoreFlowRequest对象", description = "服务业绩明细请求对象")
public class RelationScoreFlowRequest implements Serializable {

    @ApiModelProperty("用户账户")
    private String account;

    @ApiModelProperty("下单用户账户")
    private String orderAccount;

    @ApiModelProperty("单号")
    private String ordersSn;

    @ApiModelProperty("点位")
    private Integer node;

    @ApiModelProperty("方向")
    private String action;

    @ApiModelProperty(value = "付款时间区间")
    private String dateLimit;

    @ApiModelProperty("用户昵称")
    private String nickname;
}
