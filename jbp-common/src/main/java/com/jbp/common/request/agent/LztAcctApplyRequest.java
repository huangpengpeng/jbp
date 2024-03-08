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
@ApiModel(value = "LztAcctApplyRequest对象", description = "来账通账户申请请求对象")
public class LztAcctApplyRequest implements Serializable {

    @ApiModelProperty(value = "连连账户名称")
    private String username;

    @ApiModelProperty(value = "连连外部用户[本平台默认生成]")
    private String userId;


    @ApiModelProperty(value = "状态")
    private String status;
}
