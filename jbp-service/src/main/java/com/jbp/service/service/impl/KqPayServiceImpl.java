package com.jbp.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jbp.common.kqbill.invoke.BuildHttpsClient;
import com.jbp.common.kqbill.params.*;
import com.jbp.common.kqbill.result.KqPayInfoResult;
import com.jbp.common.kqbill.result.KqPayQueryResult;
import com.jbp.common.kqbill.result.KqRefundQueryResult;
import com.jbp.common.kqbill.result.KqRefundResult;
import com.jbp.common.kqbill.utils.Signature;
import com.jbp.common.utils.CrmebUtil;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.vo.MyRecord;
import com.jbp.service.service.KqPayService;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;

@Service
@Slf4j
public class KqPayServiceImpl implements KqPayService {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private BuildHttpsClient buildHttpsClient;

    @Override
    public KqPayInfoResult get() {
        MyRecord myRecord = systemConfigService.getValuesByKeyList(Lists.newArrayList("kq_merchantAcctId", "kq_applyName",
                "kq_host", "kq_terminalIp", "kq_status", "kq_page_url"));
        KqPayInfoResult result = new KqPayInfoResult();
        result.setMerchantCode(myRecord.getStr("kq_merchantCode"));
        result.setMerchantId(myRecord.getStr("kq_merchantId"));
        result.setApplyName(myRecord.getStr("kq_applyName"));
        result.setTerminalIp(myRecord.getStr("kq_terminalIp"));
        result.setHost(myRecord.getStr("kq_host"));
        result.setStatus(myRecord.getStr("kq_status"));
        result.setPageUrl(myRecord.getStr("kq_page_url"));
        result.setBgUrl(myRecord.getStr("kq_bg_url"));

        return result;
    }



    @Override
    public String cashier(String payerId, String payerIP, String orderId, BigDecimal orderAmount, String productName, String pageUrl, Date orderTime) {
        KqPayInfoResult kpInfo = get();
        KqCashierParams params = new KqCashierParams();
        params.setMerchantAcctId(kpInfo.getMerchantCode()+"01");
        params.setPageUrl(kpInfo.getPageUrl() + orderId);
        params.setBgUrl(kpInfo.getBgUrl() + orderId);
        params.setPayerId(payerId);
        params.setPayerIP(payerIP);
        params.setTerminalIp(kpInfo.getTerminalIp());
        params.setTdpformName(kpInfo.getApplyName());
        params.setOrderId(orderId);
        params.setOrderAmount(String.valueOf(orderAmount.multiply(BigDecimal.valueOf(100)).intValue()));
        params.setProductName(productName);
        params.setOrderTime(DateTimeUtils.format(orderTime, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
        params.setOrderTimestamp(params.getOrderTime());

        String signMsgVal = cashierAppendParam(params);
        String signMsg = Signature.signMsg(signMsgVal);
        signMsg = URLEncoder.encode(signMsg);
        params.setSignMsg(signMsg);

        String host = "https://sandbox.99bill.com/mobilegateway/recvMerchantInfoAction.htm";
        String url = cashierAppendParam(params);
        return host + "?" + url;
    }

    @Override
    public KqPayQueryResult queryPayResult(String orderId) {
        KqPayInfoResult payInfo = get();
        KqHeadParams head = new KqHeadParams("1.0.0", "F0003", payInfo.getMerchantCode(), CrmebUtil.getOrderNo("KQQ_"));
        KqPayQueryParams body = new KqPayQueryParams();
        body.setOrderId(orderId);
        body.setMerchantAcctId(payInfo.getMerchantCode() + "01");
        JSONObject originalString = new JSONObject();
        originalString.put("head", head);
        originalString.put("requestBody", body);
        log.info("交易查询快钱原始报文 = {}", originalString.toJSONString());
        try {
            String s = buildHttpsClient.requestKQ(originalString);
            if (StringUtils.isEmpty(s)) {
                return null;
            }
            JSONObject jsonObject = JSONObject.parseObject(s);
            JSONObject responseBody = jsonObject.getJSONObject("responseBody");
            JSONArray resultList = responseBody.getJSONArray("resultList");
            if (resultList == null || resultList.isEmpty()) {
                return null;
            }
            return resultList.getJSONObject(0).toJavaObject(KqPayQueryResult.class);
        } catch (JSONException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public KqRefundResult refund(String orderId, String refundId, BigDecimal amt, Date refundTime) {
        KqPayInfoResult payInfo = get();
        KqHeadParams head = new KqHeadParams("1.0.0", "F0001", payInfo.getMerchantCode(), refundId);
        KqRefundParams body = new KqRefundParams();
        body.setMerchantAcctId(head.getMemberCode());
        body.setAmount(String.valueOf(amt.multiply(BigDecimal.valueOf(100)).intValue()));
        body.setEntryTime(DateTimeUtils.format(refundTime, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
        body.setOrgOrderId(orderId);

        JSONObject originalString = new JSONObject();
        originalString.put("head", head);
        originalString.put("requestBody", body);
        log.info("退款快钱原始报文 = {}", originalString.toJSONString());
        try {
            String s = buildHttpsClient.requestKQ(originalString);
            if (StringUtils.isEmpty(s)) {
                return null;
            }
            JSONObject jsonObject = JSONObject.parseObject(s);
            JSONObject responseBody = jsonObject.getJSONObject("responseBody");
            if(responseBody == null){
                return null;
            }
            return responseBody.toJavaObject(KqRefundResult.class);
        } catch (JSONException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public KqRefundQueryResult queryRefundResult(String refundId, Date refundTime) {
        KqPayInfoResult payInfo = get();
        KqHeadParams head = new KqHeadParams("1.0.0", "F0002", payInfo.getMerchantCode(), CrmebUtil.getOrderNo("KQQ_"));
        KqRefundQueryParams body = new KqRefundQueryParams();
        body.setMerchantAcctId(head.getMemberCode() + "01");
        body.setStartDate(DateTimeUtils.format(DateTimeUtils.addHours(refundTime, -1), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
        body.setEndDate(DateTimeUtils.format(DateTimeUtils.addDays(refundTime, 2), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2));
        body.setOrderId(refundId);
        JSONObject originalString = new JSONObject();
        originalString.put("head", head);
        originalString.put("requestBody", body);
        log.info("退款查询快钱原始报文 = {}", originalString.toJSONString());

        try {
            String s = buildHttpsClient.requestKQ(originalString);
            if (StringUtils.isEmpty(s)) {
                return null;
            }
            JSONObject jsonObject = JSONObject.parseObject(s);
            JSONObject responseBody = jsonObject.getJSONObject("responseBody");
            if (responseBody == null) {
                return null;
            }
            JSONArray resultList = responseBody.getJSONArray("resultList");
            if (resultList == null || resultList.isEmpty()) {
                return null;
            }
            return resultList.getJSONObject(0).toJavaObject(KqRefundQueryResult.class);
        } catch (JSONException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String cashierAppendParam(KqCashierParams params) {
        String signMsgVal = "";
        signMsgVal = appendParam(signMsgVal, "inputCharset", params.getInputCharset());
        signMsgVal = appendParam(signMsgVal, "pageUrl", params.getPageUrl());
        signMsgVal = appendParam(signMsgVal, "bgUrl", params.getBgUrl());
        signMsgVal = appendParam(signMsgVal, "version", params.getVersion());
        signMsgVal = appendParam(signMsgVal, "language", params.getLanguage());
        signMsgVal = appendParam(signMsgVal, "signType", params.getSignType());
        if (StringUtils.isNotEmpty(params.getSignMsg())) {
            signMsgVal = appendParam(signMsgVal, "signMsg", params.getSignMsg());
        }
        signMsgVal = appendParam(signMsgVal, "merchantAcctId", params.getMerchantAcctId());
        signMsgVal = appendParam(signMsgVal, "payerId", params.getPayerId());
        signMsgVal = appendParam(signMsgVal, "payerIP", params.getPayerIP());
        signMsgVal = appendParam(signMsgVal, "orderId", params.getOrderId());
        signMsgVal = appendParam(signMsgVal, "orderAmount", params.getOrderAmount());
        signMsgVal = appendParam(signMsgVal, "orderTime", params.getOrderTime());
        signMsgVal = appendParam(signMsgVal, "orderTimestamp", params.getOrderTimestamp());
        signMsgVal = appendParam(signMsgVal, "productName", params.getProductName());
        signMsgVal = appendParam(signMsgVal, "payType", params.getPayType());
        signMsgVal = appendParam(signMsgVal, "redoFlag", params.getRedoFlag());
        signMsgVal = appendParam(signMsgVal, "mobileGateway", params.getMobileGateway());
        return signMsgVal;
    }

    public static String appendParam(String returns, String paramId, String paramValue) {
        if (returns != "") {
            if (paramValue != "") {
                returns += "&" + paramId + "=" + paramValue;
            }
        } else {
            if (paramValue != "") {
                returns = paramId + "=" + paramValue;
            }
        }
        return returns;
    }

}
