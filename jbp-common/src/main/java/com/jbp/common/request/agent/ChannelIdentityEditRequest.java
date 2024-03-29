package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
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
@ApiModel(value = "ChannelIdentityEditRequest对象", description = "渠道银行卡信息修改对象")
public class ChannelIdentityEditRequest  implements Serializable {

    @ApiModelProperty("编号")
    @NotNull(message = "编号不能为空")
    private Integer id  ;

    @NotBlank(message = "身份证号不能为空")
    @ApiModelProperty("身份证号码")
    private String idCardNo;

    @NotBlank(message = "真是姓名不能为空")
    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("身份证正面照片")
    private String idCardNoFrontImg;

    @ApiModelProperty("身份证反面照片")
    private String idCardNoBackImg;

    @ApiModelProperty("其他信息")
    private String otherJSON;
}
