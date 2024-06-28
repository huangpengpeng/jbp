package com.jbp.common.jdpay.vo;


import java.io.Serializable;

public class JdPayQueryOrderRequest implements Serializable {
    /**
     * 商户订单号
     */
    private String outTradeNo;


    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Override
    public String toString() {
        return "{ \"outTradeNo\":\"" + outTradeNo + "\""
                + "}"
                ;
    }
}
