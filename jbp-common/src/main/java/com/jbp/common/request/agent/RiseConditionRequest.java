package com.jbp.common.request.agent;

import com.jbp.common.model.agent.RiseCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "RiseConditionRequest对象", description = "等级升级设置")
public class RiseConditionRequest implements Serializable {

    @NotNull(message = "等级不能为空")
    @ApiModelProperty("等级ID")
    private Long capaId;

    @NotEmpty(message = "条件不能为空")
    @ApiModelProperty("条件")
    private List<RiseCondition> conditionList;

    @ApiModelProperty("升级表达式")
    private String parser;
}
