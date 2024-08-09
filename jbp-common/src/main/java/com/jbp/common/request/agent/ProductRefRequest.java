package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductRefAddRequest对象", description = "商品关联套组增加请求对象")
public class ProductRefRequest  implements Serializable {

    @NotNull(message = "套组ID不能为空")
    @ApiModelProperty("套组ID")
    private Integer refProductId;

    @ApiModelProperty("商品关联套组信息")
    private List<ProductRefAddRequest> productRefInfoList;


}
