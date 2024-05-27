package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import com.jbp.common.yop.dto.ExtParams4BankPay;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountRechargeParams extends BaseYopRequest {

    private String parentMerchantNo;

    private String merchantNo;

    private String requestNo;

    private String payType = "BANK_PAY";

    private String feeType;

    private String amount;

    private ExtParams4BankPay requestExtParams4BankPay;



}
