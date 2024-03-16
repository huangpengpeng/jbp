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
@ApiModel(value = "SelfScoreFlowRequest对象", description = "个人业绩明细请求对象")
public class SelfScoreFlowRequest implements Serializable {

    @ApiModelProperty("账户")
    private String account;

    @ApiModelProperty("方向")
    private String action;

    @ApiModelProperty("单号")
    private String ordersSn;

    @ApiModelProperty(value = "付款时间区间")
    private String dateLimit;
}
