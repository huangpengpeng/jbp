package com.jbp.common.jdpay.vo;

public class JdPayCreateOrderResponse extends BaseResponse {
    /**
     * 京东交易订单号
     */
    private String tradeNo;
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 交易类型
     */
    private String tradeType;
    /**
     * 模板号
     */
    private String templateNo;
    /**
     * 跳转收银台地址
     */
    private String webUrl;

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

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getTemplateNo() {
        return templateNo;
    }

    public void setTemplateNo(String templateNo) {
        this.templateNo = templateNo;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    @Override
    public String toString() {
        return "{\"tradeNo\":\"" + tradeNo + "\""
                + ", \"outTradeNo\":\"" + outTradeNo + "\""
                + ", \"tradeType\":\"" + tradeType + "\""
                + ", \"templateNo\":\"" + templateNo + "\""
                + ", \"webUrl\":\"" + webUrl + "\""
                + "}"
                + super.toString()
                ;
    }
}
