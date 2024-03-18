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
@ApiModel(value = "RelationScoreRequest对象", description = "服务业绩汇总请求对象")
public class RelationScoreRequest implements Serializable {
    @ApiModelProperty("用户账号")
    private String account;

    @ApiModelProperty(value = "创建时间区间")
    private String dateLimit;
}
