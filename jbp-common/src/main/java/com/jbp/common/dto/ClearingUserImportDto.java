package com.jbp.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ClearingUserImportDto对象", description="结算名单导入对象")
public class ClearingUserImportDto implements Serializable {

    @ApiModelProperty("账户")
    private String account;

    @ApiModelProperty("级别")
    private Long level;

    @ApiModelProperty("级别名称")
    private String levelName;
}
