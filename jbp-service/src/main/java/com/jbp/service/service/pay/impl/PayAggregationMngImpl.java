package com.jbp.service.service.pay.impl;

import com.jbp.common.model.pay.*;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.response.pay.PayQueryResponse;
import com.jbp.common.response.pay.PayRefundResponse;
import com.jbp.service.service.pay.PayAggregationMng;
import com.jbp.service.service.pay.channel.LianLianPaySvc;
import com.jbp.service.service.pay.channel.YopPaySvc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class PayAggregationMngImpl implements PayAggregationMng {

    @Resource
    private LianLianPaySvc lianLianPaySvc;
    @Resource
    private YopPaySvc yopPaySvc;


    @Override
    public PayCreateResponse create(PayUser payUser, PayChannel payChannel, PaySubMerchant paySubMerchant, PayUnifiedOrder order) {
        if (payChannel.getName().equals("易宝")) {
            return yopPaySvc.tradeOrder(payChannel, payUser, paySubMerchant, order);
        }
        if (payChannel.getName().equals("连连")) {
            return lianLianPaySvc.tradeOrder(payChannel, payUser, paySubMerchant, order);
        }
        return null;
    }

    @Override
    public PayQueryResponse query(PayUser payUser, PayChannel payChannel, PaySubMerchant paySubMerchant, PayUnifiedOrder order) {
        if (payChannel.getName().equals("易宝")) {
            return yopPaySvc.queryPayResult(payChannel, payUser, paySubMerchant, order);

        }
        if (payChannel.getName().equals("连连")) {
            return lianLianPaySvc.queryPayResult(payChannel, payUser, paySubMerchant, order);
        }
        return null;
    }

    @Override
    public PayRefundResponse refund(PayChannel payChannel, PayUser payUser, PaySubMerchant subMerchant,
                                    PayUnifiedOrder payOrder, PayUnifiedRefundOrder refundOrder) {
        if (payChannel.getName().equals("易宝")) {
            return yopPaySvc.refund(payChannel, payUser, subMerchant, payOrder, refundOrder);
        }
        if (payChannel.getName().equals("连连")) {
            return lianLianPaySvc.refund(payChannel, payUser, subMerchant, payOrder, refundOrder);
        }
        return null;
    }

    @Override
    public PayRefundResponse refundQuery(PayChannel payChannel, PayUser payUser, PaySubMerchant subMerchant,
                                         PayUnifiedOrder payOrder, PayUnifiedRefundOrder refundOrder) {
        if (payChannel.getName().equals("易宝")) {
            return yopPaySvc.queryRefund(payChannel, payUser, subMerchant, payOrder, refundOrder);
        }
        if (payChannel.getName().equals("连连")) {
            return lianLianPaySvc.queryRefund(payChannel, payUser, subMerchant, payOrder, refundOrder);
        }
        return null;
    }


}
