package com.jbp.common.kqbill.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class KqPayQueryResult implements Serializable {

    /**
     * 与提交时的商户订单号保持一致
     */
    private String orderId;

    /**
     * 快钱交易单号
     */
    private String txnNo;

    /**
     * 整型数字以分为单位
     */
    private String amount;

    /**
     * 订单实际支付金额
     */
    private String payAmount;

    /**
     * 商户订单提交时间
     */
    private String entryTime;
    /**
     * 交易时间
     */
    private String txnTime;

    /**
     * 12：快钱人民币账户支付
     * 13：线下支付
     * 14:B2B支付
     * 17：预付卡支付
     * 21：快捷支付
     * 26-1表示微信公众号支付、26-2表示微信WAP(暂不支持)、26-3表示小程序支付
     * 27-1表示支付宝服务窗(暂不支持)、27-2表示支付宝WAP（标准版，暂不支持）、27-3表示支付宝WAP（定制版）
     * 28-1表示微信扫码，28-2表示支付宝扫码
     * 23表示分期支付、23-2表示信用卡分期支付
     * 29表示花呗分期支付（非直连）	非必填	28
     */
    private String payType;

    /**
     * 快钱收取商户的手续费
     */
    private String fee;
}
