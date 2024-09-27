package com.jbp.common.jdpay.vo;

public class JdPayRefundResponse extends BaseResponse {
    /**
     * 京东退款订单号
     */
    private String tradeNo;
    /**
     * 商户退款订单号
     */
    private String outTradeNo;
    /**
     * 商户原正单订单号
     */
    private String originalOutTradeNo;
    /**
     * 订单总金额
     */
    private String tradeAmount;

    /**
     * 交易状态
     */
    private String tradeStatus;
    /**
     * 币种
     */
    private String currency;
    /**
     * 退款完成时间
     */
    private String finishDate;


    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getOriginalOutTradeNo() {
        return originalOutTradeNo;
    }

    public void setOriginalOutTradeNo(String originalOutTradeNo) {
        this.originalOutTradeNo = originalOutTradeNo;
    }

    public String getTradeAmount() {
        return tradeAmount;
    }

    public void setTradeAmount(String tradeAmount) {
        this.tradeAmount = tradeAmount;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    @Override
    public String toString() {
        return "{\"tradeNo\":\"" + tradeNo + "\""
                + ", \"outTradeNo\":\"" + outTradeNo + "\""
                + ", \"originalOutTradeNo\":\"" + originalOutTradeNo + "\""
                + ", \"tradeAmount\":\"" + tradeAmount + "\""
                + ", \"tradeStatus\":\"" + tradeStatus + "\""
                + ", \"currency\":\"" + currency + "\""
                + ", \"finishDate\":\"" + finishDate + "\""
                + "}"
                + super.toString()
                ;
    }

    public boolean ifSuccess() {
        if (this == null) {
            return false;
        }
        if (!"CLOS".equals(this.tradeStatus)) {
            return true;
        }
        return false;
    }
}
