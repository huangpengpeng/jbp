package com.jbp.service.service.pay;

import com.jbp.common.model.pay.PayChannel;
import com.jbp.common.model.pay.PayUnifiedOrder;
import com.jbp.common.model.pay.PayUser;
import com.jbp.common.model.pay.PayUserSubMerchant;
import com.jbp.common.request.pay.PayQueryRequest;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.response.pay.PayQueryResponse;
import com.jbp.common.response.pay.PayRefundQueryResponse;
import com.jbp.common.response.pay.PayRefundResponse;

public interface PayAggregationMng {

    PayCreateResponse create(PayUser payUser, PayChannel payChannel, PayUserSubMerchant payUserSubMerchant, PayUnifiedOrder order);

    PayQueryResponse query(PayQueryRequest request);

    PayRefundResponse refund();

    PayRefundQueryResponse refundQuery();
}
