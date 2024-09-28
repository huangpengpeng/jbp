package com.jbp.common.jdpay.vo;

import java.io.Serializable;

public class JdPayCommissionInfo implements Serializable {

    /**
     * 佣金编号
     */
    private String commNo;

    /**
     * 正单收佣金无需填此字段；退款退佣金时，此字段必填；
     */
    private String orgCommNo;

    /**
     * 分佣金额
     */
    private String amount;

    /**
     * 02
     * 在企业站入驻时配置的佣金账户，佣金账户的二级商户号
     */
    private String receiveMerchantno;


    public String getCommNo() {
        return commNo;
    }

    public void setCommNo(String commNo) {
        this.commNo = commNo;
    }

    public String getOrgCommNo() {
        return orgCommNo;
    }

    public void setOrgCommNo(String orgCommNo) {
        this.orgCommNo = orgCommNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReceiveMerchantno() {
        return receiveMerchantno;
    }

    public void setReceiveMerchantno(String receiveMerchantno) {
        this.receiveMerchantno = receiveMerchantno;
    }
}
