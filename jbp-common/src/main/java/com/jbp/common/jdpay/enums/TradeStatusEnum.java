package com.jbp.common.jdpay.enums;

public enum TradeStatusEnum {
    BUID("BUID", "交易建立"),
    WPAR("WPAR", "等待支付结果"),
    CLOS("CLOS", "交易关闭，交易失败"),
    FINI("FINI", "交易成功"),
    REFU("REFU", "交易退款"),
    ACSU("ACSU", "受理成功");

    private String code;
    private String cnName;

    private TradeStatusEnum(String code, String cnName) {
        this.code = code;
        this.cnName = cnName;
    }

    public static TradeStatusEnum getEnum(String code) {
        if (null == code) {
            return null;
        } else {
            TradeStatusEnum[] arr$ = values();
            int len$ = arr$.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                TradeStatusEnum c = arr$[i$];
                if (code.equals(c.code)) {
                    return c;
                }
            }

            throw new IllegalArgumentException("No enum code '" + code + "'. " + TradeStatusEnum.class);
        }
    }

    public String getCode() {
        return this.code;
    }

    public String getCnName() {
        return this.cnName;
    }
}
