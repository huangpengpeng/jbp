package com.jbp.service.service.pay.channel;

import com.jbp.common.lianlian.result.TradeCreateResult;
import com.jbp.common.model.pay.*;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.response.pay.PayQueryResponse;
import com.jbp.common.response.pay.PayRefundResponse;
import com.jbp.common.utils.DateTimeUtils;
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

    public PayCreateResponse tradeOrder(PayChannel payChannel, PayUser payUser, PaySubMerchant subMerchant, PayUnifiedOrder payUnifiedOrder) {
        PayCreateResponse response = new PayCreateResponse(payUser.getAppKey(), payUnifiedOrder.getPayMethod(), payUnifiedOrder.getTxnSeqno(), payUnifiedOrder.getPayAmt().toString());

        String goodsName = payUnifiedOrder.getOrderInfo().get(0).getGoodsName();
        String channelNotifyUrl = payUnifiedOrder.getChannelNotifyUrl();
        String channelReturnUrl = payUnifiedOrder.getChannelReturnUrl();
        String method = payUnifiedOrder.getPayMethod();
        String userId = payUser.getAppKey() + "_" + payUnifiedOrder.getUserNo();

        TradeOrderParams tradeOrderParams = new TradeOrderParams(subMerchant.getMerchantNo(), payUnifiedOrder.getTxnSeqno(), payUnifiedOrder.getPayAmt().toString(), goodsName, channelNotifyUrl, "", channelReturnUrl);
        tradeOrderParams.setParentMerchantNo(payChannel.getParentMerchantNo());
        TradeOrderResult tradeOrderResult = send("/rest/v1.0/trade/order", "POST", tradeOrderParams, TradeOrderResult.class);

        if ("wechatPay".equals(method)) {
            WechatAlipayPayParams wechat = new WechatAlipayPayParams(payUnifiedOrder.getTxnSeqno(), payUnifiedOrder.getPayAmt(),
                    channelNotifyUrl, "WECHAT_OFFIACCOUNT", "WECHAT", "wx788c4a29e00f3f36", "oRYS_6boFN9FOuZ0wsOCK3FpckKc", payUnifiedOrder.getIp(), "ONLINE", "REAL_TIME");
            wechat.setUniqueOrderNo(tradeOrderResult.getUniqueOrderNo());
            wechat.setToken(tradeOrderResult.getToken());
            wechat.setMerchantNo(subMerchant.getMerchantNo());
            wechat.setParentMerchantNo(tradeOrderResult.getParentMerchantNo());
            wechat.setGoodsName(goodsName);
            WechatAliPayPayResult wechatPayResult = send("/rest/v1.0/aggpay/pre-pay", "POST", wechat, WechatAliPayPayResult.class);
            response.setPayload(wechatPayResult.getPrePayTn());
            response.setPlatformTxno(wechatPayResult.getUniqueOrderNo());

        }
        if ("aliPay".equals(method)) {
            WechatAlipayPayParams alipayPay = new WechatAlipayPayParams(payUnifiedOrder.getTxnSeqno(), payUnifiedOrder.getPayAmt(),
                    channelNotifyUrl, "USER_SCAN", "ALIPAY", "", "", payUnifiedOrder.getIp(), "ONLINE", "REAL_TIME");

            alipayPay.setUniqueOrderNo(tradeOrderResult.getUniqueOrderNo());
            alipayPay.setToken(tradeOrderResult.getToken());
            alipayPay.setMerchantNo(subMerchant.getMerchantNo());
            alipayPay.setParentMerchantNo(tradeOrderResult.getParentMerchantNo());
            alipayPay.setGoodsName(goodsName);
            WechatAliPayPayResult aliPayResult = send("/rest/v1.0/aggpay/pre-pay", "POST", alipayPay, WechatAliPayPayResult.class);
            response.setPayload(aliPayResult.getPrePayTn());
            response.setPlatformTxno(aliPayResult.getUniqueOrderNo());
        }
        if ("quickPay".equals(method)) {
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
            response.setPayload(cashier);
        }
        return response;
    }


    public PayQueryResponse queryPayResult(PayChannel payChannel, PayUser payUser, PaySubMerchant subMerchant, PayUnifiedOrder payUnifiedOrder) {
        PayQueryResponse response = new PayQueryResponse(payUser.getAppKey(), payUnifiedOrder.getPayMethod(),
                payUnifiedOrder.getTxnSeqno(), payUnifiedOrder.getPayChannelSeqno(), payUnifiedOrder.getPayAmt().toString(),
                DateTimeUtils.format(payUnifiedOrder.getCreateTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
        TradeOrderQueryParams params = new TradeOrderQueryParams(payChannel.getParentMerchantNo(), subMerchant.getMerchantNo(),
                payUnifiedOrder.getTxnSeqno());
        TradeOrderQueryResult result = send("/rest/v1.0/trade/order/query", "GET", params, TradeOrderQueryResult.class);
        response.setSuccessTime(result.getPaySuccessDate());
        response.setPlatformTxno(result.getUniqueOrderNo());
        if ("SUCCESS".equals(result.getStatus())) {
            response.setStatus("SUCCESS");
        }
        if ("PROCESSING".equals(result.getStatus())) {
            response.setStatus("PROCESSING");
        }
        if ("TIME_OUT".equals(result.getStatus()) || "FAIL".equals(result.getStatus()) || "CLOSE".equals(result.getStatus())) {
            response.setStatus("FAIL");
        }
        return response;
    }

    public PayRefundResponse refund(PayChannel payChannel, PayUser payUser, PaySubMerchant subMerchant, PayUnifiedOrder payUnifiedOrder, PayUnifiedRefundOrder refundOrder) {
        PayRefundResponse response = new PayRefundResponse(payUser.getAppKey(), payUnifiedOrder.getTxnSeqno(), refundOrder.getPayRefundNo(),
                refundOrder.getRefundAmt().toString(), DateTimeUtils.format(refundOrder.getCreateTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));

        TradeRefundParams params = new TradeRefundParams(payUnifiedOrder.getTxnSeqno(), refundOrder.getPayRefundNo(), refundOrder.getRefundAmt().toString());
        params.setParentMerchantNo(payChannel.getParentMerchantNo());
        params.setMerchantNo(subMerchant.getMerchantNo());
        params.setRefundAccountType(YopEnums.AccountTypeEnum.待结算账户.getValue());

        TradeRefundResult result = send("/rest/v1.0/trade/refund", "POST", params, TradeRefundResult.class);
        if (!result.validate()) {
            params.setRefundAccountType(YopEnums.AccountTypeEnum.商户资金账户.getValue());
            result = send("/rest/v1.0/trade/refund", "POST", params, TradeRefundResult.class);
        }
        if (!result.validate()) {
            throw new RuntimeException("调用退款异常:" + refundOrder.getPayRefundNo());
        }
        if ("PROCESSING".equals(result.getStatus())) {
            response.setStatus("PROCESSING");
            response.setPlatformRefundTxno(result.getUniqueRefundNo());
        }
        if ("SUCCESS".equals(result.getStatus())) {
            response.setStatus("SUCCESS");
            response.setPlatformRefundTxno(result.getUniqueRefundNo());
            response.setSuccessTime(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
        }
        if ("FAILED".equals(result.getStatus()) || "CANCEL".equals(result.getStatus())) {
            response.setStatus("FAIL");
            response.setPlatformRefundTxno(result.getUniqueRefundNo());
        }
        return response;
    }

    public PayRefundResponse queryRefund(PayChannel payChannel, PayUser payUser, PaySubMerchant subMerchant, PayUnifiedOrder payOrder,
                                         PayUnifiedRefundOrder refundOrder) {

        PayRefundResponse response = new PayRefundResponse(payUser.getAppKey(), payOrder.getTxnSeqno(), refundOrder.getPayRefundNo(),
                refundOrder.getRefundAmt().toString(), DateTimeUtils.format(refundOrder.getCreateTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));

        com.jbp.common.yop.params.RefundQueryParams params = new RefundQueryParams(subMerchant.getMerchantNo(), payOrder.getTxnSeqno(), refundOrder.getPayRefundNo());
        params.setParentMerchantNo(payChannel.getParentMerchantNo());
        RefundQueryResult result = send("/rest/v1.0/trade/refund/query", "GET", params, RefundQueryResult.class);

        response.setPlatformRefundTxno(result.getUniqueRefundNo());
        if ("PROCESSING".equals(result.getStatus())) {
            response.setStatus("PROCESSING");
        }
        if ("SUCCESS".equals(result.getStatus())) {
            response.setStatus("SUCCESS");
            response.setSuccessTime(result.getRefundSuccessDate());
        }
        if ("FAILED".equals(result.getStatus()) || "CANCEL".equals(result.getStatus())) {
            response.setStatus("FAIL");
        }
        return response;
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
