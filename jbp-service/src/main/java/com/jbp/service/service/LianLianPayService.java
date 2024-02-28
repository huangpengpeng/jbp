package com.jbp.service.service;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.lianlian.params.*;
import com.jbp.common.lianlian.result.*;

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


    /* ######################################## 来账通接口 ##################################### */

    /**
     * 来账通开户
     */
    LztOpenacctApplyResult lztOpenacctApply(LztOpenacctApplyParams params);

    /**
     * 来账通账户查询
     */
    LztQueryAcctInfoResult lztQueryAcctInfo(String user_id);

    /**
     * 来账通账户资金划拨
     */
    LztFundTransferResult lztFundTransfer(LztFundTransferParams params);

    /**
     * 来账通账户资金划拨
     */
    LztQueryFundTransferResult lztQueryFundTransfer(String userId, String accpTxno);

    /**
     * 来账通查询连连账户信息
     */
    AcctInfoResult lztLianLianQueryAcctInfo(String userId);

    UserInfoResult lztQueryUserInfo(String userId);

    ApplyPasswordElementResult getLztPasswordElementToken(String userId, String payCode, String pyee_name, BigDecimal amount, String scan);
    /**
     * 来账通内部代发申请
     */
    TransferMorepyeeResult lztTransferMorepyee(String payerId, String orderNo, Double amt, String txnPurpose, String pwd, String randomKey, String payeeId, String ip, String notify_url);

    /**
     * 来账通内部代发结果查询
     */
    QueryPaymentResult lztQueryTransferMorepyee(String accpTxno);

    WithdrawalResult lztWithdrawal(String payeeNo, String drawNo, BigDecimal amt, String postscript, String password, String random_key, String ip, String notifyUrl);

    QueryWithdrawalResult lztQueryWithdrawal(String accpTxno);







}
