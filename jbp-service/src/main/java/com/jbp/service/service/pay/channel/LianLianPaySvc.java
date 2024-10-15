package com.jbp.service.service.pay.channel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jbp.common.lianlian.client.LLianPayClient;
import com.jbp.common.lianlian.client.LLianPayClientV2;
import com.jbp.common.lianlian.params.*;
import com.jbp.common.lianlian.result.LianLianPayInfoResult;
import com.jbp.common.lianlian.result.QueryPaymentResult;
import com.jbp.common.lianlian.result.TradeCreateResult;
import com.jbp.common.lianlian.utils.LLianPayDateUtils;
import com.jbp.common.model.pay.PayChannel;
import com.jbp.common.model.pay.PaySubMerchant;
import com.jbp.common.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
public class LianLianPaySvc {

    @Resource
    private LLianPayClientV2 llianPayClientV2;

    /**
     * 支付下单
     */
    public static TradeCreateResult tradeOrder(PayChannel payChannel, PaySubMerchant subMerchant, String userId, String notifyUrl, String returnUrl,
                                                   String payCode, Double amount, String goodsName) {
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
        CashierPayCreateParams gwParams = CashierPayCreateParams.builder().timestamp(timestamp).oid_partner(payChannel.getParentMerchantNo())
                .txn_type("GENERAL_CONSUME").user_id(userId).user_type("ANONYMOUS").notify_url(notifyUrl).return_url(returnUrl)
                .flag_chnl("H5").build();

        // 测试风控参数
        String registerTime = DateTimeUtils.format(DateTimeUtils.addMonths(new Date(), -3), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN2);
        RiskItemInfo riskItemInfo = new RiskItemInfo("4009", userId, "", registerTime, goodsName, "H5", ip, "1");
        params.setRisk_item(JSONObject.toJSONString(riskItemInfo));
        JSONObject extendJson = new JSONObject();
        extendJson.put("req_domain", "www.fnymk.com");
        params.setExtend(extendJson);
        // 设置商户订单信息
        CashierPayCreateOrderInfo orderInfo = new CashierPayCreateOrderInfo();
        orderInfo.setTxn_seqno(payCode);
        orderInfo.setTxn_time(timestamp);
        orderInfo.setTotal_amount(amount.doubleValue());
        orderInfo.setGoods_name(goodsName);
        params.setOrderInfo(orderInfo);

        // 设置付款方信息
        CashierPayCreatePayerInfo payerInfo = new CashierPayCreatePayerInfo();
        payerInfo.setPayer_id(userId);
        payerInfo.setPayer_type("USER");
        params.setPayerInfo(payerInfo);

        // 收款方
        CashierPayCreatePayeeInfo payeeInfo = new CashierPayCreatePayeeInfo();
        payeeInfo.setPayee_id("system_user_c_01"); // 不允许改变
        payeeInfo.setPayee_type("USER");
        payeeInfo.setPayee_accttype("USEROWN");
        payeeInfo.setPayee_amount(amount.toString());
        params.setPayeeInfo(new CashierPayCreatePayeeInfo[]{payeeInfo});

        String url = "https://accpapi.lianlianpay.com/v1/txn/payment-gw";
        LLianPayClient lLianPayClient = new LLianPayClient();
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        CashierPayCreateResult result = JSON.parseObject(resultJsonStr, CashierPayCreateResult.class);
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
        // 测试环境URL
        String url = "https://accpapi.lianlianpay.com/v1/txn/query-payment";
        String resultJsonStr = lLianPayClient.sendRequest(url, JSON.toJSONString(params));
        QueryPaymentResult queryPaymentResult = JSON.parseObject(resultJsonStr, QueryPaymentResult.class);
        return queryPaymentResult;
    }



    /**
     * 退款申请
     */

    /**
     * 退款查询
     */
}
