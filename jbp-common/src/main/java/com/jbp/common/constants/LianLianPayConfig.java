package com.jbp.common.constants;

import lombok.Getter;

/**
 * 连连支付配置
 */
public class LianLianPayConfig {

    public enum PayMethod {
        余额("余额", "BALANCE"),
        优惠券("优惠券", "COUPON"),
        微信APP_小程序("微信APP_小程序", "WECHAT_APP_WXA"),
        网银借记卡("网银借记卡", "EBANK_DEBIT_CARD"),
        网银信用卡("网银信用卡", "EBANK_CREDIT_CARD"),
        企业网银("企业网银", "EBANK_B2B"),
        微信APP("微信APP", "WECHAT_APP"),
        微信公众号("微信公众号", "WECHAT_JSAPI"),
        微信扫码("微信扫码", "WECHAT_NATIVE"),
        微信H5("微信H5", "WECHAT_H5"),
        微信小程序("微信小程序", "WECHAT_WXA"),
        支付宝扫码("支付宝扫码", "ALIPAY_NATIVE"),
        支付宝APP("支付宝APP", "ALIPAY_APP"),
        支付宝H5("支付宝H5", "ALIPAY_H5"),
        支付宝WEB("支付宝WEB", "ALIPAY_WEB"),
        支付宝小程序("支付宝小程序", "ALIPAY_WXA"),
        协议支付借记卡("协议支付借记卡", "AGRT_DEBIT_CARD"),
        协议支付信用卡("协议支付信用卡", "AGRT_CREDIT_CARD"),
        银行卡收银台("银行卡收银台", "BANK_CARD_PAY"),
        聚合码支付方式("聚合码支付方式", "AGGREGATE_CODE"),
        银联云闪付("银联云闪付", "CUP_YUNSHANFU"),
        微信委托代扣("微信委托代扣", "WECHAT_PAPAY"),
        支付宝商户代扣("支付宝商户代扣", "ALIPAY_PAPAY"),
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

    public enum TxnStatus {

        交易处理中("交易处理中", "TRADE_WAIT_PAY"),
        交易成功("交易成功", "TRADE_SUCCESS"),
        交易失败("交易失败", "TRADE_CLOSE"),
        ;

        @Getter
        private String name;

        @Getter
        private String code;

        TxnStatus(String name, String code) {
            this.name = name;
            this.code = code;
        }
    }






    public enum TxnPurpose {
        贷款("贷款"),
        服务费("服务费"),
        信息费("信息费"),
        修理费("修理费"),
        佣金支付("佣金支付"),
        其他("其他"),
        ;

        @Getter
        private String name;

        TxnPurpose(String name) {
            this.name = name;
        }
    }





}
