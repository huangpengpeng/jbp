package com.jbp.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户白名单对象
 */
@Data
@NoArgsConstructor
public class UserWhiteDto implements Serializable {

    @ApiModelProperty("用户账户")
    private String account;

    @ApiModelProperty("白名单名称")
    private String whiteName;
    @ApiModelProperty("单号")
    private String ordersSn;

}
