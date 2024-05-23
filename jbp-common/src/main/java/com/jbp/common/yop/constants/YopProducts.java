package com.jbp.common.yop.constants;

import com.jbp.common.utils.JacksonTool;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开户产品
 */
public class  YopProducts {

    // 企业开户产品
    public static enum Merchant {

        //        小程序支付_微信_线上("MINI_PROGRAM_WECHAT_ONLINE", "SINGLE_PERCENT", "0.75", "0.63"),
//        小程序支付_微信_线下("MINI_PROGRAM_WECHAT_OFFLINE", "SINGLE_PERCENT", "0.35", "0.23"),
//        小程序支付_支付宝_线下("MINI_PROGRAM_ALIPAY_OFFLINE", "SINGLE_PERCENT", "0.35", "0.23"),
//        用户扫码_微信_线上("USER_SCAN_WECHAT_ONLINE", "SINGLE_PERCENT", "0.75", "0.63"),
//        用户扫码_微信_线下("USER_SCAN_WECHAT_OFFLINE", "SINGLE_PERCENT", "0.35", "0.23"),
//        用户扫码_支付宝_线下("USER_SCAN_ALIPAY_OFFLINE", "SINGLE_PERCENT", "0.35", "0.23"),
//
        企业账户充值标准版_银行汇款("ENTERPRISE_RECHARGE_STANDARD_BANK_TRASFER", "SINGLE_FIXED", "10", "10"),
        //        企业账户提现标准版_实时到账("ENTERPRISE_WITHDRAW_STANDARD_REALTIME", "SINGLE_FIXED", "1", "1"),
        企业账户转账_公对公("ENTERPRISE_TRANSFER_B2B", "SINGLE_FIXED", "1", "1"),
//        企业付款_实时到账_对公("ENTERPRISE_PAYMENT_REALTIME_PUBLIC", "SINGLE_FIXED", "2", "2"),
//        企业付款_实时到账_对私("ENTERPRISE_PAYMENT_REALTIME_PRIVATE", "SINGLE_FIXED", "2", "2"),

        一键支付_借记卡("ONEKEYPAY_DEBIT", "SINGLE_PERCENT", "0.42", "0.3"),
        一键支付_贷记卡("ONEKEYPAY_CREDIT", "SINGLE_PERCENT", "0.67", "0.55"),

        D1_自助结算("D1_MANUAL", "SINGLE_FIXED", "0", "0"),
        ;
        @Getter
        private String productCode; // 产品编码

        @Getter
        private String rateType; // 分润方式

        @Getter
        private String rate; // 固定金额/ 百分比

        @Getter
        private String baseRate; // 基准值


        Merchant(String productCode, String rateType, String rate, String baseRate) {
            this.productCode = productCode;
            this.rateType = rateType;
            this.rate = rate;
            this.baseRate = baseRate;
        }
    }


    // 个人开户产品
    public static enum MicroMerchant {
//        一键支付_借记卡("ONEKEYPAY_DEBIT", "SINGLE_PERCENT", "0.6", "0.3"),
//        一键支付_贷记卡("ONEKEYPAY_CREDIT", "SINGLE_PERCENT", "1", "0.55"),
//        易宝钱包绑卡("WALLET_BINDCARD", "SINGLE_FIXED", "0", "0"),
//        易宝钱包充值("WALLET_RECHARGE", "SINGLE_FIXED", "0", "0"),
//        易宝钱包开户("WALLET_ACCOUNT", "SINGLE_FIXED", "0", "0"),
//        易宝钱包提现_实时到账("WALLET_WITHDRAW_REALTIME", "SINGLE_FIXED", "1", "0"),
//        易宝钱包支付_余额支付("WALLET_PAY_BALANCEPAY", "SINGLE_FIXED", "0", "0"),

        D1_自助结算("D1_MANUAL", "SINGLE_FIXED", "0", "0"),
        ;

        @Getter
        private String productCode; // 产品编码

        @Getter
        private String rateType; // 分润方式

        @Getter
        private String rate; // 固定金额/ 百分比

        @Getter
        private String baseRate; // 基准值


        MicroMerchant(String productCode, String rateType, String rate, String baseRate) {
            this.productCode = productCode;
            this.rateType = rateType;
            this.rate = rate;
            this.baseRate = baseRate;
        }
    }


    public static String getMerchant() {
        List<Map<String, String>> products = new ArrayList<>();
        for (Merchant pro : Merchant.values()) {
            Map<String, String> proInfo = new HashMap<>();
            proInfo.put("productCode", pro.getProductCode());
            proInfo.put("rateType", pro.getRateType());
            if (pro.getRateType().equals("SINGLE_FIXED")) {
                proInfo.put("fixedRate", pro.getRate());
            } else {
                proInfo.put("percentRate", pro.getRate());
            }
            /**
             * 费率变更必填，开户可以不填
             */
            proInfo.put("undertaker", "ORDINARY_MERCHANT");
            proInfo.put("paymentMethod", "REAL_TIME");
            products.add(proInfo);
        }
        return JacksonTool.toJsonString(products);
    }

    public static String getMicroMerchant() {
        List<Map<String, String>> products = new ArrayList<>();
        for (MicroMerchant pro : MicroMerchant.values()) {
            Map<String, String> proInfo = new HashMap<>();
            proInfo.put("productCode", pro.getProductCode());
            proInfo.put("rateType", pro.getRateType());
            if (pro.getRateType().equals("SINGLE_FIXED")) {
                proInfo.put("fixedRate", pro.getRate());
            } else {
                proInfo.put("percentRate", pro.getRate());
            }
            products.add(proInfo);
        }
        return JacksonTool.toJsonString(products);
    }

}
