package com.jbp.common.jdpay.vo;

/**
 * 无卡号签约请求
 */
public class JdPayAgreementSignRequest {
    /**
     * 签约请求号
     */
    private String signReqNo;

    /**
     * 模板号
     */
    private String templateNo;

    /**
     * 二级商户号
     */
    private String merchantNo;

    /**
     * 商户会员号
     */
    private String customerNo;

    /**
     * 支付工具
     */
    private String payTool;

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
     * 持卡人银行简码
     */
    private String bankCode;

    /**
     * 银行卡类型
     */
    private String cardType;

    /**
     * 客户端IP，无卡号签约时必传
     */
    private String clientIp;

    /**
     * 成功页完成回调地址
     */
    private String returnUrl;

    /**
     * 签约描述
     */
    private String tradeSubject;

    /**
     * 设备标识 eid
     */
    private String deviceId;

    /**
     * 用户会员类型
     */
    private String outCustomerType;

    /**
     * 用户会员编码
     */
    private String outCustomerCode;

    /**
     * 通知地址
     */
    private String notifyUrl;

    /**
     * 扩展参数(json串)
     */
    private String extendParams;

    /**
     * 回传参数
     */
    private String returnParams;

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

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getPayTool() {
        return payTool;
    }

    public void setPayTool(String payTool) {
        this.payTool = payTool;
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

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getTradeSubject() {
        return tradeSubject;
    }

    public void setTradeSubject(String tradeSubject) {
        this.tradeSubject = tradeSubject;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getExtendParams() {
        return extendParams;
    }

    public void setExtendParams(String extendParams) {
        this.extendParams = extendParams;
    }

    public String getReturnParams() {
        return returnParams;
    }

    public void setReturnParams(String returnParams) {
        this.returnParams = returnParams;
    }

    @Override
    public String toString() {
        return "JdPayAgreementSignRequest{" +
                "signReqNo='" + signReqNo + '\'' +
                ", templateNo='" + templateNo + '\'' +
                ", merchantNo='" + merchantNo + '\'' +
                ", customerNo='" + customerNo + '\'' +
                ", payTool='" + payTool + '\'' +
                ", idType='" + idType + '\'' +
                ", idNo='" + idNo + '\'' +
                ", idName='" + idName + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", cardType='" + cardType + '\'' +
                ", clientIp='" + clientIp + '\'' +
                ", returnUrl='" + returnUrl + '\'' +
                ", tradeSubject='" + tradeSubject + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", outCustomerType='" + outCustomerType + '\'' +
                ", outCustomerCode='" + outCustomerCode + '\'' +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", extendParams='" + extendParams + '\'' +
                ", returnParams='" + returnParams + '\'' +
                '}';
    }
}
