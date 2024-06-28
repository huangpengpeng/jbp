package com.jbp.common.jdpay.vo;


import java.io.Serializable;

public class JdPayRefundRequest implements Serializable {
    /**
     * 商户原交易号
     */
    private String originalOutTradeNo;
    /**
     * 商户退款单号
     */
    private String outTradeNo;
    /**
     * 退款金额
     */
    private String tradeAmount;
    /**
     * 异步通知URL
     */
    private String notifyUrl;
    /**
     * 回传字段
     */
    private String returnParams;
    /**
     * 币种
     */
    private String currency;
    /**
     * 交易时间
     */
    private String tradeDate;
    /**
     * 退款分账信息
     * @see JdPayDivisionAccountRefund
     */
    private String divisionAccountRefund;


    @Override
    public String toString() {
        return "{\"originalOutTradeNo\":\"" + originalOutTradeNo + "\""
                + ", \"outTradeNo\":\"" + outTradeNo + "\""
                + ", \"tradeAmount\":\"" + tradeAmount + "\""
                + ", \"notifyUrl\":\"" + notifyUrl + "\""
                + ", \"returnParams\":\"" + returnParams + "\""
                + ", \"currency\":\"" + currency + "\""
                + ", \"tradeDate\":\"" + tradeDate + "\""
                + ", \"divisionAccountRefund\":\"" + divisionAccountRefund + "\""
                + "}"
                ;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getReturnParams() {
        return returnParams;
    }

    public void setReturnParams(String returnParams) {
        this.returnParams = returnParams;
    }

    public String getOriginalOutTradeNo() {
        return originalOutTradeNo;
    }

    public void setOriginalOutTradeNo(String originalOutTradeNo) {
        this.originalOutTradeNo = originalOutTradeNo;
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

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getDivisionAccountRefund() {
        return divisionAccountRefund;
    }

    public void setDivisionAccountRefund(String divisionAccountRefund) {
        this.divisionAccountRefund = divisionAccountRefund;
    }
}
