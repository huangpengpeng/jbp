package com.jbp.service.service;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.lianlian.params.MorePayeeRefundResult;
import com.jbp.common.lianlian.params.TransferMorepyeeParams;
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

    WithDrawalResult withdrawal(String drawNo, BigDecimal amt, String postscript, String password, String random_key);


    /**
     * 内部代发申请
     * https://accpapi.lianlianpay.com/v1/txn/transfer-morepyee
     */
    TransferMorepyeeResult transferMorepyee(String payerId, String orderNo, Double amt, String txnPurpose, String pwd, String  randomKey, String payeeId, String ip);

}
