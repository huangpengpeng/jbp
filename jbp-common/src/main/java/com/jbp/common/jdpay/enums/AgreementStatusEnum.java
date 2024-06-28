package com.jbp.common.jdpay.enums;

public enum AgreementStatusEnum {
    BUID("BUID", "协议创建"),
    WPAR("WPAR", "协议待审核"),
    CLOS("CLOS", "协议关闭"),
    FAIL("FAIL", "协议失败"),
    FINI("FINI", "协议生效");

    private String code;
    private String cnName;

    private AgreementStatusEnum(String code, String cnName) {
        this.code = code;
        this.cnName = cnName;
    }

    public static AgreementStatusEnum getEnum(String code) {
        if (null == code) {
            return null;
        } else {
            AgreementStatusEnum[] arr$ = values();
            int len$ = arr$.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                AgreementStatusEnum c = arr$[i$];
                if (code.equals(c.code)) {
                    return c;
                }
            }

            throw new IllegalArgumentException("No enum code '" + code + "'. " + AgreementStatusEnum.class);
        }
    }

    public String getCode() {
        return this.code;
    }

    public String getCnName() {
        return this.cnName;
    }
}
