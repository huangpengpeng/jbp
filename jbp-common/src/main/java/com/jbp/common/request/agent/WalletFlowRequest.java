package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WalletFlowRequest对象", description = "用户积分详情请求对象")
public class WalletFlowRequest {
    @ApiModelProperty("用户账号")
    private String account;

    @ApiModelProperty("钱包类型")
    @TableField("type")
    private Integer type;
}
