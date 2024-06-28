package com.jbp.common.jdpay.vo;


/**
 * 交易成功通知
 */
public class JdPayAgreementQueryResponse extends BaseResponse {
    /**
     * 商户订单号
     */
    private String signReqNo;
    /**
     * 协议完成时间
     */
    private String finishDate;
    /**
     * 签约协议号
     */
    private String agreementNo;
    /**
     * 签约状态
     */
    private String agreementStatus;

    @Override
    public String toString() {
        return "{\"signReqNo\":\"" + signReqNo + "\""
                + ", \"finishDate\":\"" + finishDate + "\""
                + ", \"agreementNo\":\"" + agreementNo + "\""
                + ", \"agreementStatus\":\"" + agreementStatus + "\""
                + "}"
                + super.toString()
                ;
    }

    public String getAgreementNo() {
        return agreementNo;
    }

    public void setAgreementNo(String agreementNo) {
        this.agreementNo = agreementNo;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getAgreementStatus() {
        return agreementStatus;
    }

    public void setAgreementStatus(String agreementStatus) {
        this.agreementStatus = agreementStatus;
    }

    public String getSignReqNo() {
        return signReqNo;
    }

    public void setSignReqNo(String signReqNo) {
        this.signReqNo = signReqNo;
    }
}
