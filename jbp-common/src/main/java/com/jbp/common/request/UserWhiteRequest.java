package com.jbp.common.request;

import com.jbp.common.result.CommonResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserWhiteRequest对象", description = "用户白名单请求对象")
public class UserWhiteRequest  implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "白名单Id ")
    private Integer whiteId;
    @ApiModelProperty(value = "用户id")
    private Integer userId;

}
