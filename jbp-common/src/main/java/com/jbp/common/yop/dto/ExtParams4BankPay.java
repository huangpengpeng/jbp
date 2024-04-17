package com.jbp.common.yop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ExtParams4BankPay implements Serializable {

    public ExtParams4BankPay(String bankCode, String userRequestIP, String bankAccountNo) {
        this.bankCode = bankCode;
        this.userRequestIP = userRequestIP;
        this.bankAccountNo = bankAccountNo;
    }

    /**
     * 可选项如下:
     * XIB
     * BOL // 支持
     * FJHTB
     * WHZBB
     * XWB
     * HXBXB
     * XWB_Z
     * SUNINGBANK
     */
    private String bankCode;

    /**
     * 请求IP
     */
    private String userRequestIP;

    /**
     * 银行账户号
     * 银行编码为[XIB、FJHTB、WHZBB、XWB、HXBXB、SUNINGBANK]的时候必填
     */
    private String bankAccountNo;
}
