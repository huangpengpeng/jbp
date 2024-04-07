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
        交易关闭("交易关闭", "TRADE_CLOSE"),
        交易失败("交易失败", "TRADE_FAILURE"),
        退汇("退汇", "TRADE_CANCEL"),
        预付完成("预付完成", "TRADE_PREPAID"),

        待确认("待确认", "TRADE_CHECKING"),




        ;

        @Getter
        private String name;

        @Getter
        private String code;

        TxnStatus(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public static String getName(String code){
            for (TxnStatus value : TxnStatus.values()) {
                if(value.getCode().equals(code)){
                    return value.getName();
                }
            }
            return "";
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


    public enum AcctType {

        用户自有待结算账户("用户自有待结算账户", "USEROWN_PSETTLE"),
        用户自有可用账户("用户自有可用账户", "USEROWN_AVAILABLE"),
        平台商户自有待结算账户("平台商户自有待结算账户", "MCHOWN_PSETTLE"),
        平台商户自有可用账户("平台商户自有可用账户", "MCHOWN_AVAILABLE"),
        平台商户担保待结算账户("平台商户担保待结算账户", "MCHASSURE_PSETTLE"),
        平台商户担保可用账户("平台商户担保可用账户", "MCHASSURE_AVAILABLE"),
        平台商户优惠券待结算账户("平台商户优惠券待结算账户", "MCHCOUPON_PSETTLE"),
        平台商户优惠券可用账户("平台商户优惠券可用账户", "MCHCOUPON_AVAILABLE"),
        平台商户手续费结算账户("平台商户手续费结算账户", "MCHFEE_PSETTLE"),
        平台商户手续费可用账户("平台商户手续费可用账户", "MCHFEE_AVAILABLE"),
        银行账户_借记卡("银行账户_借记卡", "BANKCARD_DEBIT"),
        银行卡账户_信用卡("银行卡账户_信用卡", "BANKCARD_CREDIT"),
        银行账户_对公("银行账户_对公", "BANKCARD_ENTERPRISE"),
        第三方账户("第三方账户", "THIRD_PARTY"),
        ;

        @Getter
        private String name;
        @Getter
        private String code;

        AcctType(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public static String getName(String code) {
            for (AcctType value : AcctType.values()) {
                if (value.code.equals(code)) {
                    return value.getName();
                }
            }
            return "";
        }
    }


    public enum AcctState {

        NORMAL("正常"),
        CANCEL("注销"),
        PENDING("待开户"),
        WAIT_SIGN("待开户意愿确认"),
        FAIL("失败"),
        ;

        @Getter
        private String code;


        AcctState(String code) {
            this.code = code;
        }
    }


    public enum TxnSeqnoPrefix {
        来账通开通子商户("LZT_ZSH_"),
        来账通开通银行虚拟户("LZT_BKH_"),
        来账通划拨资金("LZT_FT_"),
        来账通内部代发("LZT_NDF_"),
        来账通外部代发("LZT_WDF_"),
        来账通提现("LZT_DW_"),
        设置密码("LZT_SP_"),
        ;

        @Getter
        private String prefix;

        TxnSeqnoPrefix(String prefix) {
            this.prefix = prefix;
        }
    }

    public enum UserType {
        商户("INNERMERCHANT"),
        个人用户("INNERUSER"),
        企业用户("INNERCOMPANY"),
        ;

        @Getter
        private String code;

        UserType(String code) {
            this.code = code;
        }

        public static String getCode(String name) {
            for (UserType value : UserType.values()) {
                if (value.name().equals(name)) {
                    return value.getCode();
                }
            }
            return "";
        }
    }

    public enum UserStatus {

        待开户("INIT"), // 自定义的


        已登记("ACTIVATE_PENDING"),
        正常("NORMAL"),
        审核未通过("ACTIVATE_PENDING"),
        审核中("CHECK_PENDING"),
        审核通过_待打款验证("REMITTANCE_VALID_PENDING"),
        销户("CANCEL"),
        暂停("PAUSE"),
        待激活("ACTIVATE_PENDING_NEW"),
        ;

        @Getter
        private String code;

        UserStatus(String code) {
            this.code = code;
        }

        public static String getCode(String name) {
            for (UserStatus value : UserStatus.values()) {
                if (value.name().equals(name)) {
                    return value.getCode();
                }
            }
            return "";
        }
        public static String getName(String code) {
            for (UserStatus value : UserStatus.values()) {
                if (value.getCode().equals(code)) {
                    return value.name();
                }
            }
            return "";
        }
    }


    public enum FundTransferStatus {

        创建("CREATE"), // 自定义的
        处理中("PROCESS"),
        成功("SUCCESS"),
        失败("FAIL"),
        ;

        @Getter
        private String code;

        FundTransferStatus(String code) {
            this.code = code;
        }

        public static String getCode(String name) {
            for (FundTransferStatus value : FundTransferStatus.values()) {
                if (value.name().equals(name)) {
                    return value.getCode();
                }
            }
            return "";
        }
        public static String getName(String code) {
            for (FundTransferStatus value : FundTransferStatus.values()) {
                if (value.getCode().equals(code)) {
                    return value.name();
                }
            }
            return "";
        }
    }

    public enum SerialTxnType {
        用户充值("USER_TOPUP"),
        商户充值("MCH_TOPUP"),
        普通消费("GENERAL_CONSUME"),
        担保消费("SECURED_CONSUME"),
        担保确认("SECURED_CONFIRM"),
        内部代发("INNER_FUND_EXCHANGE"),

        定向内部代发("INNER_DIRECT_EXCHANGE"),
        外部代发("OUTER_FUND_EXCHANGE"),
        账户提现("ACCT_CASH_OUT"),
        手续费收取("SERVICE_FEE"),
        手续费应收应付核销("CAPITAL_CANCEL"),
        垫资调账("ADVANCE_PAY"),
        ;

        @Getter
        private String code;

        SerialTxnType(String code) {
            this.code = code;
        }

        public static String getCode(String name) {
            for (SerialTxnType value : SerialTxnType.values()) {
                if (value.name().equals(name)) {
                    return value.getCode();
                }
            }
            return "";
        }

        public static String getName(String code) {
            for (SerialTxnType value : SerialTxnType.values()) {
                if (value.getCode().equals(code)) {
                    return value.name();
                }
            }
            return "";
        }

    }
}
