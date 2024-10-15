package com.jbp.service.service.pay;

import com.jbp.common.request.pay.PayQueryRequest;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.response.pay.PayQueryResponse;
import com.jbp.common.response.pay.PayRefundQueryResponse;
import com.jbp.common.response.pay.PayRefundResponse;

public interface PayAggregationMng {

    PayCreateResponse create();

    PayQueryResponse query(PayQueryRequest request);

    PayRefundResponse refund();

    PayRefundQueryResponse refundQuery();
}
