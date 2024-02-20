package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "SelfScoreFlowRequest对象", description = "个人业绩明细请求对象")
public class SelfScoreFlowRequest {

    @ApiModelProperty("账户")
    @TableField(exist = false)
    private String account;

    @ApiModelProperty("方向")
    @TableField("action")
    private String action;
}
