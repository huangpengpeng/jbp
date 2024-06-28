package com.jbp.common.jdpay.vo;


import java.io.Serializable;

/**
 * 签约查询
 */
public class JdPayAgreementQueryRequest implements Serializable {
    /**
     * 商户订单号
     */
    private String signReqNo;
    /**
     * 协议号
     */
    private String agreementNo;

    @Override
    public String toString() {
        return "{\"signReqNo\":\"" + signReqNo + "\""
                + ", \"agreementNo\":\"" + agreementNo + "\""
                + "}"
                ;
    }

    public String getAgreementNo() {
        return agreementNo;
    }

    public void setAgreementNo(String agreementNo) {
        this.agreementNo = agreementNo;
    }

    public String getSignReqNo() {
        return signReqNo;
    }

    public void setSignReqNo(String signReqNo) {
        this.signReqNo = signReqNo;
    }
}
