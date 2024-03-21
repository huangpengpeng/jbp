package com.jbp.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "FrontIndividualCenterConfigResponse", description = "用户个人中心返回数据")
public class FrontIndividualCenterConfigResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "账号注销")
    private Boolean accountCancel;
    @ApiModelProperty(value = "协议规则")
    private Boolean agreementRule;
    @ApiModelProperty(value = "资质证明")
    private Boolean certificationProve;
    @ApiModelProperty(value = "更改昵称")
    private Boolean nicknameChange;
    @ApiModelProperty(value = "更改手机号")
    private Boolean changePhone;
}
