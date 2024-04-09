package com.jbp.common.constants;

import lombok.Getter;

/**
 * 连连支付配置
 */
public class KqPayConfig {

    public enum PayMethod {
        快钱人民币账户支付("快钱人民币账户支付", "12"),
        线下支付("线下支付", "13"),
        B2B支付("B2B支付", "14"),
        预付卡支付("预付卡支付", "17"),
        快捷支付("快捷支付", "21"),
        微信公众号支付("微信公众号支付", "26-1"),
        微信WAP("微信WAP", "26-2"),
        微信小程序支付("微信小程序支付", "26-3"),
        支付宝服务窗("支付宝服务窗", "27-1"),
        支付宝WAP("支付宝WAP", "27-2"),
        支付宝WAP定制版("支付宝WAP定制版", "27-3"),
        微信扫码("微信扫码", "28-1"),
        支付宝扫码("支付宝扫码", "28-2"),
        分期支付("分期支付", "23"),
        信用卡分期支付("信用卡分期支付", "23-2"),
        花呗分期支付("花呗分期支付", "29"),
        借记卡支付("借记卡支付", "10"),
        ;
        @Getter
        private String name;
        @Getter
        private String code;

        PayMethod(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public static PayMethod getName(String code) {
            for (PayMethod value : PayMethod.values()) {
                if (value.getCode().equals(code)) {
                    return value;
                }
            }
            return null;
        }

    }




}
