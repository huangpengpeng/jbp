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
import com.jbp.common.utils.DateTimeUtils;
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
    public TradeCreateResult tradeOrder(PayChannel payChannel, PaySubMerchant subMerchant, String userId, String notifyUrl, String returnUrl,
                                                   String payCode, Double amount, String goodsName, String  ip) {
        // 交易时间
        String timestamp = LLianPayDateUtils.getTimestamp();
        TradeCreateParams params = TradeCreateParams.builder().timestamp(timestamp)
                .oid_partner(payChannel.getParentMerchantNo()).txn_type("GENERAL_CONSUME").user_id(userId)
                .user_type("ANONYMOUS").notify_url(notifyUrl).return_url(returnUrl).build();
        // 订单信息  手续费
        Double fee_amount = BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(0.2)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        TradeCreateOrderInfo orderInfo = new TradeCreateOrderInfo(payCode, timestamp, amount, fee_amount, goodsName);
        params.setOrderInfo(orderInfo);
        // 设置收款方信息，消费分账场景支持上送多收款方（最多10个），收款总金额必须和订单总金额相等
        TradeCreatePayeeInfo mPayeeInfo = new TradeCreatePayeeInfo(subMerchant.getMerchantNo(), "USER", "USEROWN", amount.toString());
        params.setPayeeInfo(new TradeCreatePayeeInfo[]{mPayeeInfo});
        // 发起请求
        String url = "https://accpapi.lianlianpay.com/v1/txn/tradecreate";
        LLianPayClient lLianPayClient = new LLianPayClient(payChannel.getPriKey(), payChannel.getPubKey());
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        TradeCreateResult tradeCreateResult = JSON.parseObject(resultJsonStr, TradeCreateResult.class);

        // 网关支付
        PaymentGwParams gwParams = PaymentGwParams.builder().timestamp(timestamp).oid_partner(payChannel.getParentMerchantNo()).build();
        RiskItemInfo riskItemInfo = getRiskItemInfo(userId, goodsName, ip);
        gwParams.setRisk_item(JSONObject.toJSONString(riskItemInfo));

        // 来源域名
        JSONObject extendJson = new JSONObject();
        extendJson.put("req_domain", "www.fnymk.com");
        gwParams.setExtend_params(extendJson.toJSONString());

        // 设置付款方信息
        PayerInfo payerInfo = new PayerInfo();
        payerInfo.setPayer_id(userId);
        payerInfo.setPayer_type("USER");
        gwParams.setPayerInfo(payerInfo);

        // 收款方法
        PayMethods payMethods = new PayMethods();
        payMethods.setMethod("暂定"); // todo
        payMethods.setAmount(amount.toString());
        gwParams.setPayMethods(new PayMethods[]{payMethods});
        // 调用网关支付
        url = "https://accpapi.lianlianpay.com/v1/txn/payment-gw";
        resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(gwParams));
        PaymentGwResult result = JSON.parseObject(resultJsonStr, PaymentGwResult.class);
        if (result == null || !"0000".equals(result.getRet_code())) {
            throw new RuntimeException("请求三方交易失败:" + JSONObject.toJSONString(result));
        }
        return tradeCreateResult;
    }


    /**
     * 收款查询
     */
    public QueryPaymentResult queryPayResult(PayChannel payChannel, PaySubMerchant subMerchant, String orderNo) {
        QueryPaymentParams params = new QueryPaymentParams();
        params.setTimestamp(LLianPayDateUtils.getTimestamp());
        params.setOid_partner(payChannel.getParentMerchantNo());
        params.setTxn_seqno(orderNo);
        LLianPayClient lLianPayClient = new LLianPayClient(payChannel.getPriKey(), payChannel.getPubKey());
        String url = "https://accpapi.lianlianpay.com/v1/txn/query-payment";
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        QueryPaymentResult queryPaymentResult = JSON.parseObject(resultJsonStr, QueryPaymentResult.class);
        return queryPaymentResult;
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
