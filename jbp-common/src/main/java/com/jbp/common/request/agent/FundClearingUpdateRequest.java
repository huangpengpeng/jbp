package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FundClearingUpdateRequest对象", description = "佣金发放记录修改对象")
public class FundClearingUpdateRequest implements Serializable {

    @ApiModelProperty("编号")
    private List<Long> ids;

    @ApiModelProperty("备注")
    private String remark;
}
