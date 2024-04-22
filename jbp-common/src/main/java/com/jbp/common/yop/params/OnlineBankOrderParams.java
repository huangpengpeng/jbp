package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class OnlineBankOrderParams  extends BaseYopRequest {

    public OnlineBankOrderParams(String merchantNo,  String requestNo, String amount, String payType, String bankCode) {
        this.merchantNo = merchantNo;
        this.requestNo = requestNo;
        this.amount = amount;
        this.payType = payType;
        this.bankCode = bankCode;
    }

    private String merchantNo;

    private String parentMerchantNo;

    /**
     * 充值单号
     */
    private String requestNo;
    /**
     * 充值金额
     */
    private String amount;


    /**
     * B2C:个人网银
     * B2B:企业网银
     */
    private String payType;

    /**
     * 银行编码
     */
    private String bankCode;


}
