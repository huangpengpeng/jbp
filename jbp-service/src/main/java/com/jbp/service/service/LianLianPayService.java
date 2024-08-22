package com.jbp.service.service;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.lianlian.params.*;
import com.jbp.common.lianlian.result.*;
import com.jbp.common.model.order.OrderPayChannel;

import java.math.BigDecimal;

public interface LianLianPayService {

    LianLianPayInfoResult get();

    boolean checkSign(String souceStr, String signature);

    QueryPaymentResult queryPayResult(String orderNo);

    CashierPayCreateResult cashier(String account, String payCode, BigDecimal amount, String notifyUrl, String returnUrl, String goodsName, String ip);

    MorePayeeRefundResult refund(String account, String payCode, String refundNo, BigDecimal refundAmt);

    GetRandomResult getRandom(String account);

    JSONObject getPasswordElementToken(String userId, String payCode, String pyee_name, BigDecimal amount);

    UserInfoResult queryUserInfo(String userId);

    AcctInfoResult queryAcctInfo(String userId);

    WithdrawalResult withdrawal(String drawNo, BigDecimal amt, String postscript, String password, String random_key);

    LztTransferResult transferSpanPlatform(String txn_seqno, BigDecimal total_amount, String userId, String password, String random_key, String sub_acctno, String sub_acctname);

    BindCardH5ApplyResult bindCardH5Apply(String user_id, String user_type, String bind_cardtype, String txn_seqno,
                                          String notify_url);


    /**
     * 申请免密
     * https://accpgw.lianlianpay.com/v1/txn/pap-agree-apply
     */
    LztPapAgreeApplyResult  papAgreeApply(String oidPartner, String priKey,  String user_id, PapSignInfo papSignInfo);

    /**
     * 统一支付创单
     * 	https://accpapi.lianlianpay.com/v1/txn/tradecreate
     */
    TradeCreateResult tradeCreate(OrderPayChannel payChannel,String payNo, BigDecimal total_amount,
                                  BigDecimal fee_amount, String notify_url, String  return_url, String remark);


    /**
     * 微信扫码支付
     * https://accpapi.lianlianpay.com/v1/txn/payment-gw
     */
    PaymentGwResult wechatScanPay(OrderPayChannel payChannel, String payNo, BigDecimal total_amount, BigDecimal fee_amount, String notify_url, String  return_url, String remark,String client_ip);

    /**
     * 支付宝扫码
     */
    PaymentGwResult aliScanPay(OrderPayChannel payChannel, String payNo, BigDecimal total_amount, BigDecimal fee_amount, String notify_url, String  return_url, String remark, String client_ip);






}
