package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "WhiteRequest对象", description = "白名单对象")
public class WhiteRequest  implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "白名单名称")
    private String name;
}