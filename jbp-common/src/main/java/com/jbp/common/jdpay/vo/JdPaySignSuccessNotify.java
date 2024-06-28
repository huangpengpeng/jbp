package com.jbp.common.jdpay.vo;


/**
 * 交易成功通知
 */
public class JdPaySignSuccessNotify extends BaseResponse {
    /**
     * 签约协议号
     */
    private String agreementNo;
    /**
     * 签约请求号
     */
    private String signReqNo;
    /**
     * 支付完成时间
     */
    private String finishDate;
    /**
     * 签约状态
     */
    private String agreementStatus;
    /**
     * 回传字段
     */
    private String returnParams;

    public String getSignReqNo() {
        return signReqNo;
    }

    @Override
    public String toString() {
        return "{\"agreementNo\":\"" + agreementNo + "\""
                + ", \"signReqNo\":\"" + signReqNo + "\""
                + ", \"finishDate\":\"" + finishDate + "\""
                + ", \"agreementStatus\":\"" + agreementStatus + "\""
                + ", \"returnParams\":\"" + returnParams + "\""
                + "}"
                + super.toString()
                ;
    }

    public void setSignReqNo(String signReqNo) {
        this.signReqNo = signReqNo;
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

    public String getReturnParams() {
        return returnParams;
    }

    public void setReturnParams(String returnParams) {
        this.returnParams = returnParams;
    }

    public String getAgreementNo() {
        return agreementNo;
    }

    public void setAgreementNo(String agreementNo) {
        this.agreementNo = agreementNo;
    }
}
