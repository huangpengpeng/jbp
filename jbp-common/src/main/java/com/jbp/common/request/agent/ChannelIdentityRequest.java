package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 开户请求类
 */
@Data
public class ChannelIdentityRequest implements Serializable {

    @NotBlank
    @ApiModelProperty("姓名")
    private String realName;
    @NotBlank
    @ApiModelProperty("身份证号码")
    private String IDCard;
    @NotBlank
    @ApiModelProperty("银行预留手机号")
    private String mobile;
    @NotBlank
    @ApiModelProperty("卡号")
    private String bankCardNo;
    @ApiModelProperty("银行名称")
    private String bankName;
    @ApiModelProperty("总行编码")
    private String bankCode;
    @ApiModelProperty("区编码")
    private String districtCode;
    @ApiModelProperty("详细地址")
    private String address;
    @ApiModelProperty("身份证正面")
    private String IDCardFrontUrl;
    @ApiModelProperty("身份证正面")
    private String IDCardBackUrl;

    public ChannelIdentityRequest(String realName, String IDCard, String mobile, String bankCardNo, String bankName, String bankCode, String districtCode, String address,
                                  String IDCardFrontUrl, String IDCardBackUrl) {
        this.realName = realName;
        this.IDCard = IDCard;
        this.mobile = mobile;
        this.bankCardNo = bankCardNo;
        this.bankName = bankName;
        this.bankCode = bankCode;
        this.districtCode = districtCode;
        this.address = address;
        this.IDCardFrontUrl = IDCardFrontUrl;
        this.IDCardBackUrl = IDCardBackUrl;
    }

}
