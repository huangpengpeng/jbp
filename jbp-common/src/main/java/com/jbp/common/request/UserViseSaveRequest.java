package com.jbp.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserViseSaveRequest对象", description="法大大签约")
public class UserViseSaveRequest {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "真实姓名", required = true)
    private String realName;

    @ApiModelProperty(value = "身份证", required = true)
    private String idCard;

    @ApiModelProperty(value = "合同编号", required = true)
    private String signTemplateId;

    @ApiModelProperty(value = "合同名称", required = true)
    private String signTaskSubject;
    @ApiModelProperty(value = "订单号", required = true)
    private String orderNo;
}
