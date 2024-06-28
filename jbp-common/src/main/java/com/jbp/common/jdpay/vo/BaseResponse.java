package com.jbp.common.jdpay.vo;

import java.io.Serializable;

public class BaseResponse implements Serializable {
    /**
     * 业务结果
     */
    private String resultCode;
    /**
     * 响应描述
     */
    private String resultDesc;

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

    @Override
    public String toString() {
        return "{\"resultCode\":\"" + resultCode + "\""
                + ", \"resultDesc\":\"" + resultDesc + "\""
                + "}"
                ;
    }
}
