package com.jbp.service.service.pay.impl;

import com.jbp.common.model.pay.PayChannel;
import com.jbp.common.model.pay.PayUnifiedOrder;
import com.jbp.common.model.pay.PayUser;
import com.jbp.common.model.pay.PayUserSubMerchant;
import com.jbp.common.request.pay.PayQueryRequest;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.response.pay.PayQueryResponse;
import com.jbp.common.response.pay.PayRefundQueryResponse;
import com.jbp.common.response.pay.PayRefundResponse;
import com.jbp.common.yop.result.WechatAliPayPayResult;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.YopService;
import com.jbp.service.service.pay.PayAggregationMng;
import com.jbp.service.service.pay.PayUserMng;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class PayAggregationMngImpl implements PayAggregationMng {

    @Resource
    private LianLianPayService lianLianPayService;
    @Resource
    private YopService yopService;
    @Resource
    private PayUserMng payUserMng;


    @Override
    public PayCreateResponse create(PayUser payUser, PayChannel payChannel, PayUserSubMerchant merchant, PayUnifiedOrder order) {
        PayCreateResponse response = new PayCreateResponse(payUser.getAppKey(), order.getTxnSeqno(),  order.getPayAmt().toString());
        String goodsName = order.getOrderInfo().get(0).getGoodsName();
        if(payChannel.getName().equals("易宝")){
            if ("wechatPay".equals(order.getPayMethod())) {

            }

            if ("aliPay".equals(order.getPayMethod())) {
                WechatAliPayPayResult result = yopService.wechatAlipayPay(merchant.getMerchantNo(), order.getUserNo(), order.getTxnSeqno(), order.getPayAmt().toString(),
                        goodsName,
                        order.getNotifyUrl(), order.getExt(), order.getReturnUrl(), "USER_SCAN", "ALIPAY",
                        "", "", order.getIp());
                response.setPlatformTxno(result.getUniqueOrderNo());
                response.setPayload(result.getPrePayTn());
            }

            if ("quickPay".equals(order.getPayMethod())) {

            }
        }
        if(payChannel.getName().equals("连连")){
            if ("wechatPay".equals(order.getPayMethod())) {

            }

            if ("aliPay".equals(order.getPayMethod())) {

            }

            if ("quickPay".equals(order.getPayMethod())) {

            }
        }
        return response;
    }

    @Override
    public PayQueryResponse query(PayQueryRequest request) {



        return null;
    }

    @Override
    public PayRefundResponse refund() {
        return null;
    }

    @Override
    public PayRefundQueryResponse refundQuery() {
        return null;
    }
}
