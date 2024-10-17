package com.jbp.service.service.pay;

import com.jbp.common.model.pay.*;
import com.jbp.common.request.pay.PayQueryRequest;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.response.pay.PayQueryResponse;
import com.jbp.common.response.pay.PayRefundQueryResponse;
import com.jbp.common.response.pay.PayRefundResponse;

public interface PayAggregationMng {

    PayCreateResponse create(PayUser payUser, PayChannel payChannel, PaySubMerchant paySubMerchant, PayUnifiedOrder order);

    PayQueryResponse query(PayUser payUser, PayChannel payChannel, PaySubMerchant paySubMerchant, PayUnifiedOrder order);

    PayRefundResponse refund();

    PayRefundQueryResponse refundQuery();
}
