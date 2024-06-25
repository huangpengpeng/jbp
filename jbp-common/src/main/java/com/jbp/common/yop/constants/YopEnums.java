package com.jbp.common.yop.constants;

import lombok.Getter;

public class YopEnums {

    public enum RefundStatusEnum {
        退款中("PROCESSING"),
        退款成功("SUCCESS"),
        退款失败("FAILED"),
        退款拒绝("REJECT"),
        退款撤销("CANCEL");
        private String value;

        RefundStatusEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum BusinessRoleEnum {
        标准商户("ORDINARY_MERCHANT"),
        入驻商户("SETTLED_MERCHANT"),
        平台商("PLATFORM_MERCHANT"),
        分账接收方("SHARE_MERCHANT"),
        ;
        @Getter
        private String value;

        BusinessRoleEnum(String value) {
            this.value = value;
        }
    }

    public enum SignTypeEnum{
        个体工商户("INDIVIDUAL"),
        企业("ENTERPRISE"),
        事业单位("INSTITUTION")
        ;
        @Getter
        private String value;

        SignTypeEnum(String value) {
            this.value = value;
        }
    }

    public enum LegalLicenceTypeEnum{
        法人身份证("ID_CARD"),
        护照("PASSPORT"),
        港澳居民往来内地通行证("HM_VISITORPASS"),
        台胞证("TAIWAN"),
        士兵证("SOLDIER"),
        军官证("OFFICERS"),
        ;
        @Getter
        private String value;

        LegalLicenceTypeEnum(String value) {
            this.value = value;
        }
    }

    /**
     * 开户状态申请
     */
    public enum ApplicationStatusEnum{
        申请审核中("REVIEWING"),
        申请已驳回("REVIEW_BACK"),
        协议待签署("AGREEMENT_SIGNING"),
        申请已完成("COMPLETED"),
        ;
        @Getter
        private String value;

        ApplicationStatusEnum(String value) {
            this.value = value;
        }

        public static ApplicationStatusEnum getByValue(String value) {
            for (ApplicationStatusEnum applicationStatusEnum : ApplicationStatusEnum.values()) {
                if (applicationStatusEnum.value.equals(value)) {
                    return applicationStatusEnum;
                }
            }
            return null;
        }
    }

    public enum AccountTypeEnum{
        用户钱包("WALLET_ACCOUNT"),
        待结算账户("SETTLE_ACCOUNT"),
        商户资金账户("FUND_ACCOUNT"),
        营销账户("MARKET_ACCOUNT"),
        待分账账户("DIVIDE_ACCOUNT"),
        手续费账户("FEE_ACCOUNT"),
        ;
        @Getter
        private String value;

        AccountTypeEnum(String value) {
            this.value = value;
        }

        public static AccountTypeEnum getByValue(String value) {
            for (AccountTypeEnum applicationStatusEnum : AccountTypeEnum.values()) {
                if (applicationStatusEnum.value.equals(value)) {
                    return applicationStatusEnum;
                }
            }
            return null;
        }
    }

    public enum TrxCodeEnum {
        收单("1001"),
        分账("1002"),
        分账退回("1003"),
        退款("1004"),

        充值("1005"),
        转账("1006"),
        退款撤销("1010"),

        提现("2004"),
        企业付款("2005"),
        收费("2006"),
        收费撤销("2007"),

        支付账户支付("2009"),
        日结通("2011"),
        快速实名认证("2012"),
        划拨("2026"),
        资金归集("2027"),
        营销账户退款("2028"),
        营销账户支付("2029"),

        补贴商户入账("2035"),
        OCR识别("2037"),
        OCR识别撤销("2038"),
        结算("4001"),
        ;
        @Getter
        private String value;

        TrxCodeEnum(String value) {
            this.value = value;
        }

        public static TrxCodeEnum getByValue(String value) {
            for (TrxCodeEnum trxCodeEnum : TrxCodeEnum.values()) {
                if (trxCodeEnum.value.equals(value)) {
                    return trxCodeEnum;
                }
            }
            return null;
        }
    }







}
