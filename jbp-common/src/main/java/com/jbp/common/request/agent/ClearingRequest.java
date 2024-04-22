package com.jbp.common.request.agent;

import com.jbp.common.dto.ClearingUserImportDto;
import com.jbp.common.model.agent.ClearingFinal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class ClearingRequest implements Serializable {

    @ApiModelProperty("佣金类型")
    private Integer commType;

    @ApiModelProperty("佣金名称")
    private String commName;

    @ApiModelProperty("开始时间 yyyyMMdd")
    private String startTime;

    @ApiModelProperty("结束时间 格式 yyyyMMdd")
    private String endTime;

    @ApiModelProperty("是否导入名单")
    private Boolean ifImportUser;

    @ApiModelProperty("导入名单")
    private List<ClearingUserImportDto> userList;

    private Set<String> logSet;

    private ClearingFinal clearingFinal;

}
