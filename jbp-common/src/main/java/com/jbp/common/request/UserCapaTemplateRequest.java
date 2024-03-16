package com.jbp.common.request;

import com.jbp.common.request.agent.UserCapaAddRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserCapaTemplateRequest", description="用户等级添加请求对象")
public class UserCapaTemplateRequest implements Serializable {

    @ApiModelProperty("等级ID")
    private Long capaId;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("系统描述")
    private String description;

}
