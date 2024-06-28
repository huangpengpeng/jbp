package com.jbp.common.jdpay.vo;

import java.util.Map;

/**
 * 无卡号签约响应
 */
public class JdPayAgreementSignResponse {
    /**
     * 响应码/错误码
     */
    private String resultCode;

    /**
     * 响应描述/错误描述
     */
    private String resultDesc;

    /**
     * 银行网关签约报文中的url
     */
    private String submitUrl;

    /**
     * 银行网关签约报文中的data
     */
    private Map<String, String> submitData;

    /**
     * 银行网关签约报文 无卡号签约时返回
     */
    private String formData;

    /**
     * 入款方二级商户号
     */
    private String merchantNo;

    /**
     * 入款方会员号
     */
    private String customerNo;

    /**
     * 交易描述
     */
    private String tradeSubject;

    /**
     * 支付工具
     */
    private String payTool;

    /**
     * 跳转URL地址
     */
    private String requestUrl;

    /**
     * 交易状态
     */
    private String agreementStatus;

    /**
     * 回传参数
     */
    private String returnParams;

    /**
     * 扩展参数
     */
    private String extendParams;

    /**
     * 协议号
     */
    private String agreementNo;

    /**
     * 代扣类型 ACCOUNT 账户签约
     */
    private String  withholdType;

    /**
     * 签约请求号
     */
    private String signReqNo;

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

    public String getSubmitUrl() {
        return submitUrl;
    }

    public void setSubmitUrl(String submitUrl) {
        this.submitUrl = submitUrl;
    }

    public Map<String, String> getSubmitData() {
        return submitData;
    }

    public void setSubmitData(Map<String, String> submitData) {
        this.submitData = submitData;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
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

    public String getTradeSubject() {
        return tradeSubject;
    }

    public void setTradeSubject(String tradeSubject) {
        this.tradeSubject = tradeSubject;
    }

    public String getPayTool() {
        return payTool;
    }

    public void setPayTool(String payTool) {
        this.payTool = payTool;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
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

    public String getExtendParams() {
        return extendParams;
    }

    public void setExtendParams(String extendParams) {
        this.extendParams = extendParams;
    }

    public String getAgreementNo() {
        return agreementNo;
    }

    public void setAgreementNo(String agreementNo) {
        this.agreementNo = agreementNo;
    }

    public String getWithholdType() {
        return withholdType;
    }

    public void setWithholdType(String withholdType) {
        this.withholdType = withholdType;
    }

    public String getSignReqNo() {
        return signReqNo;
    }

    public void setSignReqNo(String signReqNo) {
        this.signReqNo = signReqNo;
    }

    @Override
    public String toString() {
        return "JdPayAgreementSignResponse{" +
                "resultCode='" + resultCode + '\'' +
                ", resultDesc='" + resultDesc + '\'' +
                ", submitUrl='" + submitUrl + '\'' +
                ", submitData=" + submitData +
                ", formData='" + formData + '\'' +
                ", merchantNo='" + merchantNo + '\'' +
                ", customerNo='" + customerNo + '\'' +
                ", tradeSubject='" + tradeSubject + '\'' +
                ", payTool='" + payTool + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", agreementStatus='" + agreementStatus + '\'' +
                ", returnParams='" + returnParams + '\'' +
                ", extendParams='" + extendParams + '\'' +
                ", agreementNo='" + agreementNo + '\'' +
                ", withholdType='" + withholdType + '\'' +
                ", signReqNo='" + signReqNo + '\'' +
                '}';
    }
}
