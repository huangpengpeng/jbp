package com.jbp.common.yop.constants;


public enum YopURI {
    newUploadURI("/yos/v1.0/sys/merchant/qual/upload","POST", "子商户入网资质文件上传"),
    merchantReqister("/rest/v2.0/mer/register/saas/merchant", "POST","特约商户入网(企业/个体)"),
    personRegister("/rest/v2.0/mer/register/saas/micro","POST", "特约商户入网 小微 个人"),
    registerQuery("/rest/v2.0/mer/register/query","GET", "商户入网进度查询接口"),

    productFeeModify("/rest/v2.0/mer/product/fee/modify", "POST", "商品产品费率变更"),

    productFeeQuery("/rest/v2.0/mer/product/fee/query", "GET", "商品产品费率查询"),

    productmodifyQuery("/rest/v2.0/mer/product/modify/query", "GET", "商品产品费率进度查询"),

    merchantInfoModify("/rest/v1.0/mer/merchant/info/modify", "POST", "商户信息变更"),
    authStateQuery("/rest/v2.0/mer/auth/state/query", "GET", "商户授权状态查询接口"),
    merchantWechatAuthApply("/rest/v1.0/mer/merchant/wechatauth/apply", "POST", "微信实名认证申请单申请"),

    merchantWechatAuthQuery("/rest/v1.0/mer/merchant/wechatauth/query", "GET", "查询微信实名认证状态"),

    merchantWechatAuthCancel("/rest/v1.0/mer/merchant/wechatauth/cancel", "POST", "撤回实名认证"),


    wechatconfigadd("/rest/v2.0/aggpay/wechat-config/add", "POST", "公众号配置接口更新"),

    wechatconfigquery("/rest/v2.0/aggpay/wechat-config/query", "GET", "公众号配置接口查询"),

    cashierAddress("https://cash.yeepay.com/cashier/std","GET", "标准收银台接口"),

    tradeOrder("/rest/v1.0/trade/order", "POST", "交易下单"),

    tradeorderquery("/rest/v1.0/trade/order/query", "GET", "交易下单查询"),

    prePay("/rest/v1.0/aggpay/pre-pay", "POST", "聚合支付统一下单"),
    payLink("/rest/v1.0/aggpay/pay-link", "POST", "聚合支付订单码扫码付款"),

    tutelagepayLink("/rest/v1.0/aggpay/tutelage/pre-pay", "POST", "微信支付托管下单"),
    getcardbin("/rest/v1.0/frontcashier/getcardbin", "GET", "银行卡bin识别"),

    bindcard("/rest/v2.0/frontcashier/bindcard/request", "POST", "绑卡"),

    bindcardresendsms("/rest/v2.0/frontcashier/bindcard/resendsms", "POST", "绑卡短信重发"),

    bindcardlist("/rest/v1.0/frontcashier/bindcard/bindcardlist", "GET", "绑卡列表"),

    bindpay("/rest/v1.0/frontcashier/bindpay/request", "POST", "绑卡支付下单"),

    bindpaysendsms("/rest/v1.0/frontcashier/bindpay/sendsms", "POST", "绑卡支付发短信"),

    bindpayconfirm("/rest/v1.0/frontcashier/bindpay/confirm", "POST", "绑卡支付确认"),

    /**
     * 商户充值
     */
    rechargeOrder("/rest/v1.0/account/recharge/order","POST", "充值下单"),

    /**
     * 商户充值查询
     */
    rechargeorderquery("rest/v1.0/account/recharge/query","GET", "充值查询"),

    transfer("/rest/v1.0/account/transfer/b2b/order", "POST", "转账下单"),
    transferquery("/rest/v1.0/account/transfer/system/query", "GET", "转账查询"),
    transferwechat("//rest/v1.0/account/transfer/wechat/order", "POST", "转账微信钱包"),
    transferwechatquery("/rest/v1.0/account/transfer/wechat/query", "GET", "转账微信钱包查询"),
    divideApply("/rest/v1.0/divide/apply", "POST", "申请分账"),
    divideComplete("/rest/v1.0/divide/complete", "POST", "完结分账"),
    selfSettleApply("/rest/v1.0/settle/self-settle/apply", "POST","自助结算申请"),
    bindCard("/rest/v1.0/account/withdraw/card/bind", "POST","添加提现卡"),
    withdrawCardQuery("/rest/v1.0/account/withdraw/card/query", "GET", "提现卡查询"),
    withdraw("/rest/v1.0/account/withdraw/order", "POST", "提现下单"),
    withdrawquery("/rest/v1.0/account/withdraw/system/query", "GET", "提现查询"),

    withdrawcardModify("/rest/v1.0/account/withdraw/card/modify", "POST", "提现卡修改"),

    tradeRefund("/rest/v1.0/trade/refund", "POST", "申请退款"),
    queryRefund("/rest/v1.0/trade/refund/query", "GET", "退款查询"),
    balanceQuery("/rest/v1.0/account/accountinfos/query", "GET", "账户余额的查询"),
    fundBalanceQuery("/rest/v1.0/account/balance/query", "GET","资金账户余额"),
    banktransferpay("/rest/v1.0/frontcashier/bank-transfer/pay", "POST","银行转账支付"),



    walletaccountopen("/rest/v1.0/m-wallet/account/open", "POST","开通钱包"),

    walletpasswordmanage("/rest/v1.0/m-wallet/password/manage", "POST","钱包密码设置"),

    walletcardquery("/rest/v1.0/m-wallet/card/query", "POST","钱包绑卡、查询"),

    walletrechargeinitiate("/rest/v1.0/m-wallet/recharge/initiate", "POST","钱包充值"),

    walletrechargequery("/rest/v1.0/m-wallet/recharge/query", "GET","钱包充值查询"),

    walletwithdrawinitiate("/rest/v1.0/m-wallet/withdraw/initiate", "POST","钱包提现"),

    walletwithdrawquery("/rest/v1.0/m-wallet/withdraw/query", "GET","钱包提现查询"),

    walletaccountquery("/rest/v1.0/m-wallet/account/query", "GET","钱包账户查询"),

    walletagreement("/rest/v1.0/m-wallet/agreement/payment-sign", "POST","钱包免密支付签约"),

    walletagreementcancel("/rest/v1.0/m-wallet/agreement/payment-cancel", "POST","钱包免密支付签约取消"),

    wallettradeorder("/rest/v2.0/m-wallet/trade/order", "POST","钱包交易下单 免密协议"),

    wallettransfer("/rest/v1.0/m-wallet/transfer/b2c/initiate", "POST","转账到钱包"),

    wallettransferquery("/rest/v1.0/m-wallet/transfer/b2c/query", "POST","转账到钱包查询"),





    paymentOrder("/rest/v1.0/account/pay/order","POST", "付款下单"),
    tradeOrderStd("/rest/v1.0/std/trade/order","POST","订单处理器--创建订单"),
    divideQuery("/rest/v1.0/divide/query","GET", "查询分账结果"),
    divideBack("/rest/v1.0/divide/back", "POST", "申请分账资金归还"),
    divideBackQuery(" /rest/v1.0/divide/back/query", "GET", "查询分账资金归还结果"),
    customsApply("/rest/v1.0/kj/customs/order", "POST", "海关订单申报"),
    transferSend("/rest/v1.0/balance/transfer_send", "POST", "代付代发-单笔出款请求"),
    notifyRepeat("/rest/v2.0/mer/notify/repeat", "POST", "重复获取短验 邮件电子签章"),

    ;

    private String value;
    private String method;
    private String desc;

    YopURI(String value, String method, String desc) {
        this.value = value;
        this.method = method;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public String getMethod(){
        return this.method;
    }
}
