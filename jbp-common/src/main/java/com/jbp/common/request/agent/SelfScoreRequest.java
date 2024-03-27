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
@ApiModel(value = "SelfScoreRequest对象", description = "个人业绩汇总请求对象")
public class SelfScoreRequest implements Serializable {
    @ApiModelProperty("账户")
    private String account;

}
