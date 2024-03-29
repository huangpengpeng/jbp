package com.jbp.common.request.agent;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ChannelCardEditRequest对象", description = "渠道银行卡修改对象")
public class ChannelCardEditRequest {

    @NotNull(message = "编号不能为空")
    @ApiModelProperty("编号")
    private Integer id;


    @NotBlank(message = "银行卡名称不能为空")
    @ApiModelProperty("银行名称")
    private String bankName;

    @ApiModelProperty("银行卡号")
    @NotBlank(message = "银行卡号不能为空")
    private String bankCardNo;


    @ApiModelProperty("银行编码  总行编号")
    private String bankId;

    @ApiModelProperty("银行卡预留手机号")
    @NotBlank(message = "银行卡预留手机号不能为空")
    private String phone;

    @ApiModelProperty("银行卡类型")
    @NotBlank(message = "银行卡类型不能为空")
    private String type;

    @ApiModelProperty("开户行支行编码  联行卡号")
    private String branchId;

    @ApiModelProperty("开户行支行名字")
    private String branchName;

    @ApiModelProperty("开户省份")
    private String province;


    @ApiModelProperty("开户城市")
    private String city;


}
