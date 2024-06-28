package com.jbp.common.jdpay.vo;

import java.io.Serializable;

/**
 * 分账交易信息
 */
public class JdPayDivisionAccountRefundInfo implements Serializable {
    /**
     * 分账子商户号
     */
    private String merchantNo;
    /**
     * 分账子商户订单号
     */
    private String outTradeNo;
    /**
     * 分账子商户业务单号
     */
    private String bizTradeNo;
    /**
     * 分账子单金额
     */
    private String tradeAmount;
    /**
     * 原商户订单号
     */
    private String originalOutTradeNo;

    public String getOriginalOutTradeNo() {
        return originalOutTradeNo;
    }

    public void setOriginalOutTradeNo(String originalOutTradeNo) {
        this.originalOutTradeNo = originalOutTradeNo;
    }

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getBizTradeNo() {
        return bizTradeNo;
    }

    public void setBizTradeNo(String bizTradeNo) {
        this.bizTradeNo = bizTradeNo;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    @Override
    public String toString() {
        return "{\"JdPayDivisionAccountRefundInfo\":{"
                + "\"merchantNo\":\"" + merchantNo + "\""
                + ", \"outTradeNo\":\"" + outTradeNo + "\""
                + ", \"bizTradeNo\":\"" + bizTradeNo + "\""
                + ", \"tradeAmount\":\"" + tradeAmount + "\""
                + ", \"originalOutTradeNo\":\"" + originalOutTradeNo + "\""
                + "}}";
    }
}
