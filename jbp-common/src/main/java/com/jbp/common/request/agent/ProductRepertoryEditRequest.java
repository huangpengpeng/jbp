package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProductRepertoryEditRequest对象", description = "库存编辑对象")
public class ProductRepertoryEditRequest implements Serializable {

    @ApiModelProperty(value = "id")
    @NotNull(message = "附言不能为空")
    private Integer id;

    @ApiModelProperty(value = "类型: 增加 减少")
    @NotBlank(message = "类型不能为空")
    private String kind;

    @ApiModelProperty(value = "库存数")
    @NotNull(message = "库存数不能为空")
    private Integer count;

    @ApiModelProperty(value = "附言")
    @NotBlank(message = "附言不能为空")
    private String description;
}
