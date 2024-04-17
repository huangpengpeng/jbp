package com.jbp.common.yop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class AccountInfoDto implements Serializable {
    // {"bankAccountType":"银行账户类型","bankCardNo":"银行账户号码","bankCode":"开户总行编码"}
    // ACCOUNT(结算到支付账户)
    //BANKCARD(结算到银行账户)
    private String settlementDirection = "BANKCARD";
    private String bankAccountType = "DEBIT_CARD";

    private String bankCardNo;
    private String bankCode;
}
