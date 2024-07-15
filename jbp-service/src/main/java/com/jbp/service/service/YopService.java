package com.jbp.service.service;

import com.jbp.common.yop.params.*;
import com.jbp.common.yop.result.*;
import org.springframework.web.multipart.MultipartFile;

public interface YopService {


    /**
     * H5注册个人入网
     */
    RegisterMicroH5Result registerMicroH5(RegisterMicroH5Params params);


    /**
     * 小微入网
     * /rest/v2.0/mer/register/saas/micro
     */
    RegisterMicroResult registerMicro(String requestNo, String signName, String id_card, String frontUrl,
                                      String backUrl, String mobile, String province, String city, String district,
                                      String address, String bankCardNo, String bankCode, String notifyUrl, String withdrawalUndertaker);


    /**
     * 企业入网
     */
    RegisterResult register(RegisterParams params);


    /**
     * 入网进度查询
     * /rest/v2.0/mer/register/query
     */
    RegisterQueryResult registerQuery(String requestNo);

    /**
     * 商户信息变更
     * /rest/v1.0/mer/merchant/info/modify
     */
    MerchantInfoModifyResult merchantInfoModify(MerchantInfoModifyParams params);


    String upload(String url);

    String upload(MultipartFile file);

    /**
     * 银行开户
     */
    BankAccountOpenResult bankAccountOpen(BankAccountOpenParams params);


    /**
     * 充值下单
     */
    OnlineBankOrderResult onlineBankOrder(OnlineBankOrderParams params);

    /**
     * 开户进度查询
     */
    BankAccountQueryResult bankAccountQuery(String merchantNo, String requestNo);

    /**
     * 查询银行余额
     */
    BankAccountBalanceQueryResult bankAccountBalanceQuery(String merchantNo, String bankCode, String accountNo);

    /**
     * 查询资金账户余额
     */
    AccountBalanceQueryResult accountBalanceQuery(String merchantNo);


    /**
     * 查询全部余额
     * /rest/v1.0/account/accountinfos/query
     */
    AllAccountBalanceQueryResult allAccountBalanceQuery(String merchantNo);

    /**
     * 修改提现卡
     * /rest/v1.0/account/withdraw/card/modify
     */
    WithdrawCardModifyResult withdrawCardModify(WithdrawCardModifyParams params);

    /**
     * 提现卡绑定
     * /rest/v1.0/account/withdraw/card/bind
     */
    WithdrawCardBindResult withdrawCardBind(WithdrawCardBindParams params);


    /**
     * 提现卡bin查询
     */
    WithdrawCardQueryResult withdrawCardQuery(String merchantNo);

    /**
     * 提现下单
     */
    WithdrawOrderResult withdrawOrder(String parentMerchantNo, String merchantNo, String requestNo, String bankCardId, String orderAmount, String notifyUrl);

    /**
     * 提现查询
     */
    WithdrawOrderQueryResult withdrawOrderQuery(String merchantNo, String requestNo);

    /**
     * 充值 --- 划拨
     */
    AccountRechargeResult accountRecharge(String merchantNo, String requestNo, String amount, String bankCode, String bankAccountNo);

    /**
     * 充值 --- 划拨 查询
     */
    AccountRechargeQueryResult accountRechargeQuery(String merchantNo, String requestNo);

    /**
     * 转账
     */
    AccountTransferOrderResult transferB2bOrder(String requestNo, String fromMerchantNo, String toMerchantNo, String orderAmount, String notifyUrl);

    /**
     * 转账查询
     */
    AccountTransferOrderQueryResult transferB2bOrderQuery(String merchantNo, String requestNo);

    /**
     * 代付
     */
    AccountPayOrderResult accountPayOrder(String merchantNo, String requestNo,
                                          String orderAmount, String receiverAccountName,
                                          String receiverAccountNo, String receiverBankCode,
                                          String bankAccountType, String branchBankCode, String notifyUrl);

    /**
     * 代付查询
     */
    AccountPayOrderQueryResult accountPayOrderQuery(String merchantNo, String requestNo);

    /**
     * 资金流水
     */
    FundBillFlowQueryResult fundBillFlowQuery(String parentMerchantNo, String startDate, String endDate, String merchantNo, Integer page, Integer size);


    /**
     * 回执下载
     */
    AccountReceiptResult accountReceiptGet(String merchantNo, String orderNo, String requestNo, String tradeType, String orderData);


    /**
     * 交易下单
     */
    TradeOrderResult tradeOrder(String merchantNo, String orderId, String orderAmount, String goodsName, String notifyUrl, String memo, String redirectUrl);

    /**
     * 快捷支付
     */
    String quickPay(String merchantNo, String userNo, String orderId, String orderAmount, String goodsName, String notifyUrl, String memo, String redirectUrl);

    /**
     * 微信支付宝支付
     */
    WechatAliPayPayResult wechatAlipayPay(String merchantNo, String userNo, String orderId, String orderAmount, String goodsName,
                                          String notifyUrl, String memo, String redirectUrl, String payWay, String channel,
                                          String appId, String openId, String ip);

    /**
     * 微信支付宝托管下单
     */
    WechatAlipayTutelagePayResult wechatAlipayTutelagePay(String merchantNo, String orderId, String orderAmount, String goodsName,
                                                          String notifyUrl, String redirectUrl, String payWay, String channel,
                                                          String userIp, String memo);

    /**
     * 查询支付结果
     */
    TradeOrderQueryResult queryPayResult(String merchantNo, String orderId);


    /**
     * 退款
     */
    TradeRefundResult tradeRefund(String merchantNo, String orderId, String refundOrderId, String amt);


    /**
     * 查询退款结果
     */
    RefundQueryResult refundQuery(String merchantNo, String orderId, String refundOrderId);

}
