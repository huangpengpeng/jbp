package com.jbp.common.yop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PayerInfoDto implements Serializable {

    private static final long serialVersionUID = -6290420653873281702L;

    private String bankId; // 银行编号

    private String accountName; // 账户名称.网银B2B支付会返回付款企业账户名称

    private String bankCardNo; // 银行卡号（前6后4）

    private String mobilePhoneNo; // 手机号（前3后4)

    /**
     * 卡类型
     * DEBIT：借记卡CREDIT：贷记卡CFT：微信零钱QUASI_CREDIT：准贷记卡PUBLIC_ACCOUNT：对公账户（网银B2B支付返回）
     */
    private String cardType;

    /**
     * 用户ID
     * 微信支付返回openID
     * 支付宝支付返回userID
     * 银联支付返回userID
     */
    private String userID;

}
