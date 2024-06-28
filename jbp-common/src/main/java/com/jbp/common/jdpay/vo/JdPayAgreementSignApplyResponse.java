package com.jbp.common.jdpay.vo;

public class JdPayAgreementSignApplyResponse {
    /**
     * 响应码/错误码
     */
    private String resultCode;

    /**
     * 响应描述/错误描述
     */
    private String resultDesc;

    /**
     * 跳转URL地址
     */
    private String requestUrl;

    /**
     * 签约请求号
     */
    private String signReqNo;

    /**
     * 交易描述
     */
    private String tradeSubject;

    /**
     * 完成时间
     */
    private String finishDate;

    /**
     * 回传参数
     */
    private String returnParams;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getSignReqNo() {
        return signReqNo;
    }

    public void setSignReqNo(String signReqNo) {
        this.signReqNo = signReqNo;
    }

    public String getTradeSubject() {
        return tradeSubject;
    }

    public void setTradeSubject(String tradeSubject) {
        this.tradeSubject = tradeSubject;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getReturnParams() {
        return returnParams;
    }

    public void setReturnParams(String returnParams) {
        this.returnParams = returnParams;
    }

    @Override
    public String toString() {
        return "JdPayAgreementSignResponse{" +
                "resultCode='" + resultCode + '\'' +
                ", resultDesc='" + resultDesc + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", signReqNo='" + signReqNo + '\'' +
                ", tradeSubject='" + tradeSubject + '\'' +
                ", finishDate='" + finishDate + '\'' +
                ", returnParams='" + returnParams + '\'' +
                '}';
    }



}
