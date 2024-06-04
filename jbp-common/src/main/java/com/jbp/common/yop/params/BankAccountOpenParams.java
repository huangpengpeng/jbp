package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import com.jbp.common.yop.dto.SnMultiChannelOpenAccountDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class BankAccountOpenParams extends BaseYopRequest {

    // 请求流水号
    private String requestNo;

    // 服务商商户号 // 10089066338
    private String parentMerchantNo;

    // 查询商户号  10090225827
    private String merchantNo;

    private String merchantName; // 请传商户编号对应的商户签约名，与营业执照保持一致

    private String openBankCode; // 新网：XWB_Z 百信：HXBXB_GATHER 苏宁：SUNINGBANK_MULTICHANNEL

    private String openAccountType; // ENTERPRISE:企业 INDIVIDUAL_BUSINESS_TYPE:个体工商户

    private String certificateType; // BUSINESS_LICENCE:营业执照 (银行编码为XIB,FJHTB,WHZBB,XWB时必填)

    private String certificateNo; // 证件号(银行编码为XIB,FJHTB,WHZBB,XWB时必填）

    private String notifyUrl;

    private SnMultiChannelOpenAccountDTO snMultiChannelOpenAccountDTO; // 苏宁银行开户
}
