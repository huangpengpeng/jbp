package com.jbp.common.yop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class BankCardAccountDto implements Serializable {
    /**
     * 银行卡类型
     * DEBIT_CARD:借记卡
     * ENTERPRISE_ACCOUNT:对公账户
     */
    private String bankCardType;

    /**
     * 开户名:
     * 返回商户的提现卡-开户名称
     * 示例值：易宝支付有限公司
     */
    private String accountName;


    /**
     * 开户行编码:
     * 返回商户的提现卡-开户银行编码
     * ICBC
     */
    private String bankCode;

    /**
     * 银行账号:
     * 返回商户的提现卡-银行账号
     * 示例值：6222020403021234567
     */

    private String accountNo;

    /**
     * 银行卡标识:
     * 返回易宝生成的银行卡唯一标识
     * 示例值：32453
     */
    private String bindCardId;

    /**
     * 支行编码
     */
    private String branchBankCode;
}
