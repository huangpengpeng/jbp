package com.jbp.common.jdpay.vo;

public class JdPayAgreementSignApplyRequest {
    /**
     * 签约请求号
     */
    private String signReqNo;

    /**
     * 模板号
     */
    private String templateNo;

    /**
     * 用户会员类型
     */
    private String outCustomerType;

    /**
     * 用户会员编码
     */
    private String outCustomerCode;

    /**
     * 成功页完成回调地址
     */
    private String returnUrl;

    /**
     * 通知地址
     */
    private String notifyUrl;

    /**
     * 签约描述
     */
    private String tradeSubject;

    /**
     * 证件类型
     */
    private String idType;

    /**
     * 证件号
     */
    private String idNo;

    /**
     * 姓名
     */
    private String idName;

    /**
     * 回传参数
     */
    private String returnParams;

    /**
     * 客户端IP，无卡号签约时必传
     */
    private String clientIp;

    /**
     * 付款频次：具体数字、不定期
     */
    private Integer tradeFrequency;

    /**
     * 付款周期：年、季、月、周、日、不定期
     */
    private String tradePeriod;

    /**
     * 付款用途
     */
    private String tradeUsage;

    /**
     * 协议有效期
     */
    private String expiryDate;

    /**
     * 客户端类型
     */
    private String clientType;

    public String getSignReqNo() {
        return signReqNo;
    }

    public void setSignReqNo(String signReqNo) {
        this.signReqNo = signReqNo;
    }

    public String getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(String templateNo) {
        this.templateNo = templateNo;
    }

    public String getOutCustomerType() {
        return outCustomerType;
    }

    public void setOutCustomerType(String outCustomerType) {
        this.outCustomerType = outCustomerType;
    }

    public String getOutCustomerCode() {
        return outCustomerCode;
    }

    public void setOutCustomerCode(String outCustomerCode) {
        this.outCustomerCode = outCustomerCode;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getTradeSubject() {
        return tradeSubject;
    }

    public void setTradeSubject(String tradeSubject) {
        this.tradeSubject = tradeSubject;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getReturnParams() {
        return returnParams;
    }

    public void setReturnParams(String returnParams) {
        this.returnParams = returnParams;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public Integer getTradeFrequency() {
        return tradeFrequency;
    }

    public void setTradeFrequency(Integer tradeFrequency) {
        this.tradeFrequency = tradeFrequency;
    }

    public String getTradePeriod() {
        return tradePeriod;
    }

    public void setTradePeriod(String tradePeriod) {
        this.tradePeriod = tradePeriod;
    }

    public String getTradeUsage() {
        return tradeUsage;
    }

    public void setTradeUsage(String tradeUsage) {
        this.tradeUsage = tradeUsage;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    @Override
    public String toString() {
        return "JdPayAgreementSignRequest{" +
                "signReqNo='" + signReqNo + '\'' +
                ", templateNo='" + templateNo + '\'' +
                ", outCustomerType='" + outCustomerType + '\'' +
                ", outCustomerCode='" + outCustomerCode + '\'' +
                ", returnUrl='" + returnUrl + '\'' +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", tradeSubject='" + tradeSubject + '\'' +
                ", idType='" + idType + '\'' +
                ", idNo='" + idNo + '\'' +
                ", idName='" + idName + '\'' +
                ", returnParams='" + returnParams + '\'' +
                ", clientIp='" + clientIp + '\'' +
                ", tradeFrequency=" + tradeFrequency +
                ", tradePeriod='" + tradePeriod + '\'' +
                ", tradeUsage='" + tradeUsage + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", clientType='" + clientType + '\'' +
                '}';
    }




}