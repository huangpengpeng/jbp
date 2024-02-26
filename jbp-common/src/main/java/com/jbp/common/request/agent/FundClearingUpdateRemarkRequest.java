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
@ApiModel(value = "FundClearingUpdateRemarkRequest对象", description = "佣金发放记录请求对象")
public class FundClearingUpdateRemarkRequest implements Serializable {
    @ApiModelProperty("编号")
    private Long id;
    @ApiModelProperty("备注")
    private String remark;

}
