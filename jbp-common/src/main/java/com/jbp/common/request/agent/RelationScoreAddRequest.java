package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RelationScoreAddRequest对象", description = "服务业绩汇总添加对象")
public class RelationScoreAddRequest {

    @ApiModelProperty("用户账号")
    private String account;

    @ApiModelProperty("点位")
    private int node;
}
