package com.jbp.common.jdpay.vo;

/**
 *
 *三方聚合下单响应
 */
public class JdPayAggregateCreateOrderResponse extends BaseResponse {
    /**
     * 商户号
     */
    private String merchantNo;
    /**
     * 京东交易订单号
     */
    private String tradeNo;
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 跳转收银台链接
     */
    private String webUrl;
    /**
     * 唤起通道参数
     */
    private String payInfo;

    public String getMerchantNo() {
        return merchantNo;
    }

    public void setMerchantNo(String merchantNo) {
        this.merchantNo = merchantNo;
    }

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

    public String getPayInfo() {
        return payInfo;
    }

    public void setPayInfo(String payInfo) {
        this.payInfo = payInfo;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    @Override
    public String toString() {
        return "{\"UnifiedAggregateCreateOrderResponse\":"
                + super.toString()
                + ", \"merchantNo\":\"" + merchantNo + "\""
                + ", \"tradeNo\":\"" + tradeNo + "\""
                + ", \"outTradeNo\":\"" + outTradeNo + "\""
                + ", \"webUrl\":\"" + webUrl + "\""
                + ", \"payInfo\":\"" + payInfo + "\""
                + "}";
    }
}
