package com.jbp.common.request.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "YopBankApplyRequest对象", description = "易宝银行开户")
public class YopBankApplyRequest  implements Serializable {

    @ApiModelProperty("签约名称【营业执照全称】")
    private String signName;

    @ApiModelProperty("法人身份证号码")
    private String id_card;

    @ApiModelProperty("身份证照片（正）")
    private String frontUrl;

    @ApiModelProperty("身份证照片（反）")
    private String backUrl;

    @ApiModelProperty("银行预留手机号")
    private String mobile;

    @ApiModelProperty("省 下拉选编号")
    private String province;

    @ApiModelProperty("市 下拉选编号")
    private String city;

    @ApiModelProperty("区 下拉选编号")
    private String district;

    @ApiModelProperty("详细地址 中文")
    private String address;

    @ApiModelProperty("结算卡号")
    private String bankCardNo;

    @ApiModelProperty("银行编码")
    private String bankCode;

}
