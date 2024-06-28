package com.jbp.common.jdpay.vo;


import java.io.Serializable;

/**
 * 代扣请求
 */
public class JdPayAgreementPayRequest implements Serializable {
    /**
     * 协议号
     */
    private String agreementNo;
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 交易金额
     */
    private String tradeAmount;

    /**
     * 交易币种
     */
    private String currency;

    /**
     * 交易描述
     */
    private String tradeSubject;
    /**
     * 异步通知地址
     */
    private String notifyUrl;
    /**
     * 回传参数（json串）
     */
    private String returnParams;
    /**
     * 订单超时时间:(分钟)
     */
    private String tradeExpiryTime;
    /**
     * 商品信息list-- json串
     */
    private String goodsInfo;
    /**
     * 业务分类码
     */
    private String categoryCode;
    /**
     * 业务类型
     */
    private String bizTp;

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getBizTp() {
        return bizTp;
    }

    public void setBizTp(String bizTp) {
        this.bizTp = bizTp;
    }

    public String getGoodsInfo() {
        return goodsInfo;
    }

    public void setGoodsInfo(String goodsInfo) {
        this.goodsInfo = goodsInfo;
    }

    public String getAgreementNo() {
        return agreementNo;
    }

    public void setAgreementNo(String agreementNo) {
        this.agreementNo = agreementNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTradeSubject() {
        return tradeSubject;
    }

    public void setTradeSubject(String tradeSubject) {
        this.tradeSubject = tradeSubject;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnParams() {
        return returnParams;
    }

    public void setReturnParams(String returnParams) {
        this.returnParams = returnParams;
    }

    public String getTradeExpiryTime() {
        return tradeExpiryTime;
    }

    public void setTradeExpiryTime(String tradeExpiryTime) {
        this.tradeExpiryTime = tradeExpiryTime;
    }

    @Override
    public String toString() {
        return "{\"agreementNo\":\"" + agreementNo + "\""
                + ", \"outTradeNo\":\"" + outTradeNo + "\""
                + ", \"tradeAmount\":\"" + tradeAmount + "\""
                + ", \"currency\":\"" + currency + "\""
                + ", \"tradeSubject\":\"" + tradeSubject + "\""
                + ", \"notifyUrl\":\"" + notifyUrl + "\""
                + ", \"returnParams\":\"" + returnParams + "\""
                + ", \"tradeExpiryTime\":\"" + tradeExpiryTime + "\""
                + ", \"goodsInfo\":\"" + goodsInfo + "\""
                + ", \"categoryCode\":\"" + categoryCode + "\""
                + ", \"bizTp\":\"" + bizTp + "\""
                + "}"
                ;
    }
}
