package com.jbp.service.service.pay.channel;

import com.jbp.common.model.pay.PayChannel;
import com.jbp.common.model.pay.PaySubMerchant;
import com.jbp.common.utils.JacksonTool;
import com.jbp.common.yop.BaseYopRequest;
import com.jbp.common.yop.BaseYopResponse;
import com.jbp.common.yop.constants.YopEnums;
import com.jbp.common.yop.params.*;
import com.jbp.common.yop.result.*;
import com.yeepay.yop.sdk.security.DigestAlgEnum;
import com.yeepay.yop.sdk.security.rsa.RSA;
import com.yeepay.yop.sdk.security.rsa.RSAKeyUtils;
import com.yeepay.yop.sdk.service.common.YopClient;
import com.yeepay.yop.sdk.service.common.request.YopRequest;
import com.yeepay.yop.sdk.service.common.response.YopResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class YopPaySvc {

    @Resource
    private YopClient yopClient;

    public TradeOrderResult tradeOrder(PayChannel payChannel, PaySubMerchant subMerchant, String userId, String notifyUrl, String returnUrl,
                                       String payCode, Double amount, String goodsName, String ip) {
        TradeOrderParams tradeOrderParams = new TradeOrderParams(subMerchant.getMerchantNo(), payCode, amount.toString(), goodsName, notifyUrl, "", returnUrl);
        tradeOrderParams.setParentMerchantNo(payChannel.getParentMerchantNo());
        TradeOrderResult tradeOrderResult = send("/rest/v1.0/trade/order", "POST", tradeOrderParams, TradeOrderResult.class);
        // 易宝收银台地址
        Map<String, String> params = new HashMap<>();
        params.put("appKey", "app_10089066338");
        params.put("merchantNo", tradeOrderResult.getParentMerchantNo());
        params.put("token", tradeOrderResult.getToken());
        params.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        params.put("directPayType", "YJZF");
        params.put("cardType", "");
        params.put("userNo", userId);
        params.put("userType", "USER_ID");
        params.put("ext", "");
        StringBuilder sb = new StringBuilder();
        String[] CASHIER = {"appKey", "merchantNo", "token", "timestamp", "directPayType", "cardType", "userNo", "userType", "ext"};
        for (int i = 0; i < CASHIER.length; i++) {
            String name = CASHIER[i];
            String value = params.get(name);
            if (i != 0) {
                sb.append("&");
            }
            sb.append(name).append("=").append(value);
        }
        PrivateKey privateKey = RSAKeyUtils.string2PrivateKey(payChannel.getPriKey());
        String sign = RSA.sign(sb.toString(), privateKey, DigestAlgEnum.SHA256) + "$SHA256";
        String cashier = "https://cash.yeepay.com/cashier/std" + "?sign=" + sign + "&" + sb;


        // 易宝聚合支付下单
        // WechatAlipayPayParams.PAYWAY.USER_SCAN.name()
        WechatAlipayPayParams wechatAlipayPayParams = new WechatAlipayPayParams(payCode, new BigDecimal(amount),
                notifyUrl, "支付方式", "H5_PAY", "ONLINE", "", ip, "ONLINE", "REAL_TIME");
        wechatAlipayPayParams.setUniqueOrderNo(tradeOrderResult.getUniqueOrderNo());
        wechatAlipayPayParams.setToken(tradeOrderResult.getToken());
        wechatAlipayPayParams.setMerchantNo(subMerchant.getMerchantNo());
        wechatAlipayPayParams.setParentMerchantNo(tradeOrderResult.getParentMerchantNo());
        WechatAliPayPayResult wechatAliPayPayResult = send("/rest/v1.0/aggpay/pre-pay", "POST", wechatAlipayPayParams, WechatAliPayPayResult.class);

        return null;
    }


    public TradeOrderQueryResult queryPayResult(PayChannel payChannel, PaySubMerchant subMerchant, String orderNo) {
        TradeOrderQueryParams params = new TradeOrderQueryParams(payChannel.getParentMerchantNo(), subMerchant.getMerchantNo(), orderNo);
        return send("/rest/v1.0/trade/order/query", "GET", params, TradeOrderQueryResult.class);
    }

    public TradeRefundResult refund(PayChannel payChannel, PaySubMerchant subMerchant, String userId, String payCode, String refundNo, BigDecimal refundAmt) {
        TradeRefundResult tradeRefundResult = new TradeRefundResult();
        // 支付支付订单
        TradeOrderQueryResult tradeOrderQueryResult = queryPayResult(payChannel, subMerchant, payCode);
        if (tradeOrderQueryResult == null || !tradeOrderQueryResult.ifSuccess()) {
            throw new RuntimeException("订单未支付成功不允许退款:" + payCode);
        }
        // 是否已经退款
        RefundQueryResult refundQueryResult = queryRefund(payChannel, subMerchant, payCode, refundNo);
        if (refundQueryResult != null && !StringUtils.isEmpty(refundQueryResult.getCode())) {
            if (refundQueryResult.ifSuccess()) {
                tradeRefundResult.setStatus("SUCCESS");
                return tradeRefundResult;
            }
            if (!refundQueryResult.ifCanRefund()) {
                tradeRefundResult.setStatus("PROCESSING");
                return tradeRefundResult;
            }
        }
        TradeRefundParams params = new TradeRefundParams(payCode, refundNo, refundAmt.toString());
        params.setParentMerchantNo(tradeOrderQueryResult.getParentMerchantNo());
        params.setMerchantNo(subMerchant.getMerchantNo());
        params.setRefundAccountType(YopEnums.AccountTypeEnum.待结算账户.getValue());
        tradeRefundResult = send("/rest/v1.0/trade/refund", "POST", params, TradeRefundResult.class);
        if (!tradeRefundResult.validate()) {
            params.setRefundAccountType(YopEnums.AccountTypeEnum.商户资金账户.getValue());
            tradeRefundResult = send("/rest/v1.0/trade/refund", "POST", params, TradeRefundResult.class);
        }
        if (!tradeRefundResult.validate()) {
            throw new RuntimeException("调用退款异常:" + refundNo);
        }
        return tradeRefundResult;
    }

    public RefundQueryResult queryRefund(PayChannel payChannel, PaySubMerchant subMerchant, String payCode, String refundNo) {
        com.jbp.common.yop.params.RefundQueryParams params = new RefundQueryParams(subMerchant.getMerchantNo(), payCode, refundNo);
        params.setParentMerchantNo(payChannel.getParentMerchantNo());
        return send("/rest/v1.0/trade/refund/query", "GET", params, RefundQueryResult.class);
    }


    public <T> T send(String url, String method, BaseYopRequest parameters, Class<T> responseClass) {
        //生成易宝请求
        YopRequest request = new YopRequest(url, method);
        //设置参数
        Map<String, Object> mapObj = JacksonTool.objectToMap(parameters);
        for (Map.Entry<String, Object> entry : mapObj.entrySet()) {
            if (entry.getValue() != null) {
                request.addParameter(entry.getKey(), entry.getValue());
            }
        }
        String requestText = JacksonTool.toJsonString(request.getParameters().asMap());
        log.info("易宝请求参数" + requestText);
        try {
            YopResponse response = yopClient.request(request);
            String responseText = JacksonTool.toJsonString(response);
            log.info("易宝返回参数" + responseText);
            //结果转换成对应的response
            BaseYopResponse resp = (BaseYopResponse) JacksonTool.toObject(response.getStringResult(), responseClass);
            return (T) resp;
        } catch (Exception e) {
            log.error("易宝请求异常:" + e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }


    public <T> T send2(String url, String method, BaseYopRequest parameters, Class<T> responseClass) {
        //生成易宝请求
        YopRequest request = new YopRequest(url, method);
        request.setContent(JacksonTool.toJsonString(parameters));
        log.info("易宝请求参数" + JacksonTool.toJsonString(parameters));
        try {
            YopResponse response = yopClient.request(request);
            String responseText = JacksonTool.toJsonString(response);
            log.info("易宝返回参数" + responseText);
            //结果转换成对应的response
            BaseYopResponse resp = (BaseYopResponse) JacksonTool.toObject(response.getStringResult(), responseClass);
            return (T) resp;
        } catch (Exception e) {
            log.error("易宝请求异常:" + e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
