package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FundClearingRecordUpdateRequest对象", description = "佣金统计记录修改对象")
public class FundClearingRecordUpdateRequest implements Serializable {

    @ApiModelProperty("编号")
    @NotEmpty(message = "编号不能为空")
    private List<Long> ids;
}
