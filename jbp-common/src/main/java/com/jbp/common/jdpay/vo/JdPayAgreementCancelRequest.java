package com.jbp.common.jdpay.vo;


import java.io.Serializable;

/**
 * 解约
 */
public class JdPayAgreementCancelRequest implements Serializable {
    /**
     * 协议号
     */
    private String agreementNo;

    public String getAgreementNo() {
        return agreementNo;
    }

    public void setAgreementNo(String agreementNo) {
        this.agreementNo = agreementNo;
    }


    @Override
    public String toString() {
        return "{ \"agreementNo\":\"" + agreementNo + "\""
                + "}"
                ;
    }
}
