package com.jbp.service.service.pay.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.jbp.common.lianlian.client.LLianPayClient;
import com.jbp.common.lianlian.params.*;
import com.jbp.common.lianlian.result.PaymentGwResult;
import com.jbp.common.lianlian.result.QueryPaymentResult;
import com.jbp.common.lianlian.result.RefundQueryResult;
import com.jbp.common.lianlian.result.TradeCreateResult;
import com.jbp.common.lianlian.utils.LLianPayDateUtils;
import com.jbp.common.model.pay.PayChannel;
import com.jbp.common.model.pay.PaySubMerchant;
import com.jbp.common.model.pay.PayUnifiedOrder;
import com.jbp.common.model.pay.PayUser;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.response.pay.PayQueryResponse;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class LianLianPaySvc {

    /**
     * 支付下单
     */
    public PayCreateResponse tradeOrder(PayChannel payChannel, PayUser payUser, PaySubMerchant subMerchant, PayUnifiedOrder payUnifiedOrder) {
        PayCreateResponse response = new PayCreateResponse(payUser.getAppKey(), payUnifiedOrder.getPayMethod(), payUnifiedOrder.getTxnSeqno(), payUnifiedOrder.getPayAmt().toString());

        String goodsName = payUnifiedOrder.getOrderInfo().get(0).getGoodsName();
        String channelNotifyUrl = payUnifiedOrder.getChannelNotifyUrl();
        String channelReturnUrl = payUnifiedOrder.getChannelReturnUrl();
        String method = payUnifiedOrder.getPayMethod();
        String userId = payUser.getAppKey() + "_" + payUnifiedOrder.getUserNo();

        // 交易时间
        String timestamp = LLianPayDateUtils.getTimestamp();
        TradeCreateParams params = TradeCreateParams.builder().timestamp(timestamp)
                .oid_partner(payChannel.getParentMerchantNo()).txn_type("GENERAL_CONSUME").user_id(userId)
                .user_type("ANONYMOUS").notify_url(channelNotifyUrl).return_url(channelReturnUrl).build();
        // 订单信息  手续费
        Double fee_amount = payUnifiedOrder.getPayAmt().multiply(BigDecimal.valueOf(0.2)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        TradeCreateOrderInfo orderInfo = new TradeCreateOrderInfo(payUnifiedOrder.getTxnSeqno(), timestamp, payUnifiedOrder.getPayAmt().doubleValue(), fee_amount, goodsName);
        params.setOrderInfo(orderInfo);
        TradeCreatePayeeInfo mPayeeInfo = new TradeCreatePayeeInfo(subMerchant.getMerchantNo(), "USER", "USEROWN", payUnifiedOrder.getPayAmt().toString());
        params.setPayeeInfo(new TradeCreatePayeeInfo[]{mPayeeInfo});
        // 发起请求
        String url = "https://accpapi.lianlianpay.com/v1/txn/tradecreate";
        LLianPayClient lLianPayClient = new LLianPayClient(payChannel.getPriKey(), payChannel.getPubKey());
        lLianPayClient.sendRequest(url, JSON.toJSONString(params));

        // 网关支付
        PaymentGwParams gwParams = PaymentGwParams.builder().timestamp(timestamp).oid_partner(payChannel.getParentMerchantNo()).build();
        RiskItemInfo riskItemInfo = getRiskItemInfo(userId, goodsName, payUnifiedOrder.getIp());
        gwParams.setRisk_item(JSONObject.toJSONString(riskItemInfo));
        // 来源域名
        JSONObject extendJson = new JSONObject();
        extendJson.put("req_domain", "zs.jubaopeng.cc");
        gwParams.setExtend_params(extendJson.toJSONString());

        // 设置付款方信息
        PayerInfo payerInfo = new PayerInfo();
        payerInfo.setPayer_id(userId);
        payerInfo.setPayer_type("USER");
        gwParams.setPayerInfo(payerInfo);

        // 收款方法  WECHAT_H5  ALIPAY_NATIVE  BANK_CARD_PAY
        PayMethods payMethods = new PayMethods();
        if ("wechatPay".equals(method)) {
            payMethods.setMethod("WECHAT_H5");
        }
        if ("aliPay".equals(method)) {
            payMethods.setMethod("ALIPAY_NATIVE");
        }
        if ("quickPay".equals(method)) {
            payMethods.setMethod("BANK_CARD_PAY");
        }
        payMethods.setAmount(payUnifiedOrder.getPayAmt().toString());
        gwParams.setPayMethods(new PayMethods[]{payMethods});
        // 调用网关支付
        url = "https://accpapi.lianlianpay.com/v1/txn/payment-gw";
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(gwParams));
        PaymentGwResult result = JSON.parseObject(resultJsonStr, PaymentGwResult.class);
        if (result == null || !"0000".equals(result.getRet_code())) {
            throw new RuntimeException("请求三方交易失败:" + JSONObject.toJSONString(result));
        }
        if (StringUtils.isEmpty(result.getPayload())) {
            response.setPayload(result.getGateway_url());
        } else {
            response.setPayload(result.getPayload());
        }
        response.setPlatformTxno(result.getAccp_txno());
        return response;
    }


    /**
     * 收款查询
     */
    public PayQueryResponse queryPayResult(PayChannel payChannel, PayUser payUser, PaySubMerchant subMerchant, PayUnifiedOrder payUnifiedOrder) {
        PayQueryResponse response = new PayQueryResponse(payUser.getAppKey(), payUnifiedOrder.getPayMethod(),
                payUnifiedOrder.getTxnSeqno(), payUnifiedOrder.getPayChannelSeqno(), payUnifiedOrder.getPayAmt().toString(),
                DateTimeUtils.format(payUnifiedOrder.getCreateTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));

        QueryPaymentParams params = new QueryPaymentParams();
        params.setTimestamp(LLianPayDateUtils.getTimestamp());
        params.setOid_partner(payChannel.getParentMerchantNo());
        params.setTxn_seqno(payUnifiedOrder.getTxnSeqno());
        LLianPayClient lLianPayClient = new LLianPayClient(payChannel.getPriKey(), payChannel.getPubKey());
        String url = "https://accpapi.lianlianpay.com/v1/txn/query-payment";
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        QueryPaymentResult result = JSON.parseObject(resultJsonStr, QueryPaymentResult.class);

        response.setPlatformTxno(result.getAccp_txno());
        if (StringUtils.isNotEmpty(result.getFinish_time())) {
            try {
                Date date = DateTimeUtils.parseDate(result.getFinish_time());
                response.setSuccessTime(DateTimeUtils.format(date, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
            } catch (Exception e) {
                response.setSuccessTime(DateTimeUtils.format(DateTimeUtils.getNow(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
            }
        }
        if ("TRADE_WAIT_PAY".equals(result.getTxn_status())) {
            response.setStatus("PROCESSING");
        }
        if ("TRADE_SUCCESS".equals(result.getTxn_status())) {
            response.setStatus("SUCCESS");
        }
        if ("TRADE_CLOSE".equals(result.getTxn_status())) {
            response.setStatus("FAIL");
        }
        return response;
    }



    /**
     * 退款申请
     */
    public MorePayeeRefundResult refund(PayChannel payChannel, PaySubMerchant subMerchant, String userId, String payCode, String refundNo, BigDecimal refundAmt) {

        QueryPaymentResult queryPaymentResult = queryPayResult(payChannel, subMerchant, payCode);
        if (queryPaymentResult == null || !"TRADE_SUCCESS".equals(queryPaymentResult.getTxn_status())) {
            throw new RuntimeException("订单未支付成功不允许退款:" + payCode);
        }

        QueryPaymentOrderInfo orderInfo = queryPaymentResult.getOrderInfo();
        List<QueryPaymentPayeeInfo> payeeInfo = queryPaymentResult.getPayeeInfo();
        List<QueryPaymentPayerInfo> payerInfo = queryPaymentResult.getPayerInfo();

        MorePayeeRefundParams params = new MorePayeeRefundParams();
        String timestamp = LLianPayDateUtils.getTimestamp();
        params.setTimestamp(timestamp);
        params.setOid_partner(payChannel.getParentMerchantNo());
        params.setUser_id(userId);

        // 原商户订单信息
        OriginalOrderInfo originalOrderInfo = new OriginalOrderInfo();
        // 原支付交易商户系统唯一交易流水号
        originalOrderInfo.setTxn_seqno(payCode);
        // 订单总金额
        originalOrderInfo.setTotal_amount(orderInfo.getTotal_amount());
        params.setOriginalOrderInfo(originalOrderInfo);

        // 退款订单信息
        RefundOrderInfo refundOrderInfo = new RefundOrderInfo();
        // 退款订单号。标识一次退款请求，商户系统需要保证唯一
        refundOrderInfo.setRefund_seqno(refundNo);
        refundOrderInfo.setRefund_time(timestamp);
        // 退款总金额。本次需要退款的金额，不允许超过对应原收款方的收款金额
        refundOrderInfo.setRefund_amount(refundAmt.doubleValue());
        params.setRefundOrderInfo(refundOrderInfo);

        // 原收款方退款信息
        PyeeRefundInfo pyeeRefundInfo = new PyeeRefundInfo();
        // 原收款方id，本次退款需要处理的原交易收款方id
        pyeeRefundInfo.setPayee_id(payeeInfo.get(0).getPayee_id());
        pyeeRefundInfo.setPayee_type(payeeInfo.get(0).getPayee_type());
        pyeeRefundInfo.setPayee_accttype("MERCHANT".equals(pyeeRefundInfo.getPayee_type()) ? "MCHOWN" : "USEROWN");
        // 退款金额。本次需要退款的金额，不允许超过对应原收款方的收款金额。
        pyeeRefundInfo.setPayee_refund_amount(refundAmt.doubleValue());
        // 垫资标识。当原收款方金额不足时，是否由平台垫资的标识，默认:N
        pyeeRefundInfo.setIs_advance_pay("N");
        params.setPyeeRefundInfos(Arrays.asList(new PyeeRefundInfo[]{pyeeRefundInfo}));
        // 原付款方式退款规则信息
        RefundMethod refundMethod = new RefundMethod();
        // 付款方式
        refundMethod.setMethod(payerInfo.get(0).getMethod());
        // 退款金额
        refundMethod.setAmount(refundAmt.doubleValue());
        params.setRefundMethods(Arrays.asList(new RefundMethod[]{refundMethod}));

        String url = "https://accpapi.lianlianpay.com/v1/txn/more-payee-refund";
        LLianPayClient lLianPayClient = new LLianPayClient(payChannel.getPriKey(), payChannel.getPubKey());
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        MorePayeeRefundResult morePayeeRefundResult = JSON.parseObject(resultJsonStr, MorePayeeRefundResult.class);
        return morePayeeRefundResult;
    }

    /**
     * 退款查询
     */
    public RefundQueryResult queryRefund(PayChannel payChannel, PaySubMerchant subMerchant, String payCode, String refundNo) {
        RefundQueryParams params = new RefundQueryParams();
        params.setTimestamp(LLianPayDateUtils.getTimestamp());
        params.setOid_partner(payChannel.getParentMerchantNo());
        params.setRefund_seqno(refundNo);
        String url = "https://accpapi.lianlianpay.com/v1/txn/query-refund";
        LLianPayClient lLianPayClient = new LLianPayClient(payChannel.getPriKey(), payChannel.getPubKey());
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        RefundQueryResult result = JSON.parseObject(resultJsonStr, RefundQueryResult.class);
        return result;
    }

    private static RiskItemInfo getRiskItemInfo(String userId, String goodsName, String ip) {
        // 设置分控参数【造假】
        Map<String, Object> map = SqlRunner.db().selectOne("SELECT * FROM `tmp_lian_lian_risk_user` WHERE id >= (SELECT floor( RAND() * ((SELECT MAX(id) FROM `tmp_lian_lian_risk_user`)-(SELECT MIN(id) FROM `tmp_lian_lian_risk_user`)) + (SELECT MIN(id) FROM `tmp_lian_lian_risk_user`))) ORDER BY id LIMIT 1 ");
        String registerTime = DateTimeUtils.format(DateTimeUtils.addMonths(new Date(), -3), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        RiskItemInfo riskItemInfo = new RiskItemInfo("4009", userId, MapUtils.getString(map, "phone"), registerTime, goodsName);
        riskItemInfo.setFrms_client_chnl("16");
        riskItemInfo.setFrms_ip_addr(ip);
        riskItemInfo.setUser_auth_flag("1");
        riskItemInfo.setUser_info_full_name(MapUtils.getString(map, "name"));
        riskItemInfo.setUser_info_id_no(MapUtils.getString(map, "cardNo"));
        riskItemInfo.setUser_info_identify_state("1");
        riskItemInfo.setUser_info_identify_type("1");
        riskItemInfo.setUser_info_id_type("0");
        riskItemInfo.setDelivery_full_name(MapUtils.getString(map, "name"));
        riskItemInfo.setDelivery_phone(MapUtils.getString(map, "phone"));
        riskItemInfo.setDelivery_addr_province(MapUtils.getString(map, "province"));
        riskItemInfo.setDelivery_addr_city(MapUtils.getString(map, "city"));
        return riskItemInfo;
    }
}
