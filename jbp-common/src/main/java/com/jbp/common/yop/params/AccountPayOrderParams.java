package com.jbp.common.yop.params;

import com.jbp.common.yop.BaseYopRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class AccountPayOrderParams extends BaseYopRequest {

    private String parentMerchantNo;

    String merchantNo;

    String requestNo;

    String orderAmount;


    String feeChargeSide = "PAYEE:";  // PAYER:付款方  PAYEE:收款方

    String receiveType = "REAL_TIME"; // 实时到账

    String receiverAccountName; // 开户名

    String receiverAccountNo; // 卡号

    String receiverBankCode; // 银行代码

    /**
     * 收款方账户类型
     * 账户类型
     * 可选项如下:
     * DEBIT_CARD:借记卡
     * CREDIT_CARD:贷记卡
     * QUASI_CREDIT_CARD:准贷卡
     * PASSBOOK:存折
     * UNIT_SETTLE_CARD:单位结算卡
     * PUBLIC_CARD:对公卡
     */
    String bankAccountType;

    String branchBankCode; // 支行编码

    String comments; // 附言

    String notifyUrl;

}
