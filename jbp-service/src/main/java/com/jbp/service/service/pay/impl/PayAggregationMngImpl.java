package com.jbp.service.service.pay.impl;

import com.jbp.common.request.pay.PayQueryRequest;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.response.pay.PayQueryResponse;
import com.jbp.common.response.pay.PayRefundQueryResponse;
import com.jbp.common.response.pay.PayRefundResponse;
import com.jbp.service.service.LianLianPayService;
import com.jbp.service.service.YopService;
import com.jbp.service.service.pay.PayAggregationMng;
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
    private LianLianPayService lianLianPayService;
    @Resource
    private YopService yopService;


    @Override
    public PayCreateResponse create() {
        return null;
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
