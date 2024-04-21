package com.jbp.common.request.agent;

import com.jbp.common.dto.ClearingUserImportDto;
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
@ApiModel(value="ClearingUserImportDto对象", description="结算名单导入对象")
public class ClearingPreUserRequest implements Serializable {

    @ApiModelProperty("佣金类型")
    private Integer commType;

    @ApiModelProperty("佣金名称")
    private String commName;

    @ApiModelProperty("导入名单")
    private List<ClearingUserImportDto> userList;
}
