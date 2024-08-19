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
@ApiModel(value = "ProductRepertoryAllotSearchRequest对象", description = "用户库存查询对象")
public class ProductRepertoryAllotSearchRequest implements Serializable {

    @ApiModelProperty(value = "账号")
    private String account;

}
