package com.jbp.common.jdpay.vo;


/**
 * 解约
 */
public class JdPayAgreementCancelResponse extends BaseResponse {
    /**
     * 商户订单号
     */
    private String signReqNo;

    /**
     * 签约状态
     */
    private String agreementStatus;

    /**
     * 签约协议号
     */
    private String agreementNo;

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

    public String getAgreementStatus() {
        return agreementStatus;
    }

    public void setAgreementStatus(String agreementStatus) {
        this.agreementStatus = agreementStatus;
    }

    @Override
    public String toString() {
        return "{\"signReqNo\":\"" + signReqNo + "\""
                + ", \"agreementStatus\":\"" + agreementStatus + "\""
                + ", \"agreementNo\":\"" + agreementNo + "\""
                + "}"
                + super.toString()
                ;
    }
}
