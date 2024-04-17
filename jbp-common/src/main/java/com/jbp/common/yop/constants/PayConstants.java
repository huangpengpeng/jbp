package com.jbp.common.yop.constants;

/**
 * 回调节点
 */
public class PayConstants {

    public enum CallbackEndpoint{
        register,  //商户注册
        microRegister, //个人用户注册
        productFeeModify, // 费率变更
        merchantInfoModify, // 开户信息变更

        pay,   //支付

        recharge,  //充值
        withdrawal, //提现
        payment,  //付款
        refund, //退款
        clearing, //结算
        transfer, //转账
        customs, //报关,
        settle,  //结算
        prepay, //聚合支付
    }
}
