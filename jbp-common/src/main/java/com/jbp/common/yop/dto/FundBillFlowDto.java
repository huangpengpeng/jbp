package com.jbp.common.yop.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class FundBillFlowDto implements Serializable {

    private String accountTime; // 记账时间

    private String trxTime; // 交易时间

    private String trxFlowNo; // 易宝流水号

    private String trxCode; // 业务类型

    private String merchantNo; // 商户编号

    private String merchantName; // 商户名称

    private String orderId; // 商户订单号

    private String fee; // 手续费(元),
    private String income; // 收入金额(元)

    private String expenditure; //  支出金额(元)


    private String balance; // 账户余额(元)


    private String remark;


    private String accountType; // 账户类型


    private String orgOrderId; // 原商户订单号 本次业务对应的原商户请求号


    private String bankOrderId; // 银行单号

    private String paymentNo;//  支付单号

    private String payerAccountName;// 付款方账户名称

    private String payerTel;// 付款方手机号

    private String payerAccountNo; // 付款方账号


    private String payerBankAccType; // 付款方账户类型

    private String payerBank;// 付款方银行

    private String goodsName;// 商品名称

    private String payeeAccountName;// 收款方账户名称

    private String payeeAccountNo;//收款方账号

    private String payeeBankAccType;// 收款方账户类型

    private String payeeBank;//收款方银行

    private String tradeDesc;//交易描述

    private String orderAmount;//交易金额

}
