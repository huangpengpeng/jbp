package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@Accessors(chain = true)
@ApiModel(value = "WalletRequest对象", description = "用户积分请求对象")
public class WalletRequest {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("用户ID")
    private Long uid;
    @ApiModelProperty("钱包类型")
    private Integer type;

}
