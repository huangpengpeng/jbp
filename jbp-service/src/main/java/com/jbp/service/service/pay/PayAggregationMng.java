package com.jbp.service.service.pay;

import com.jbp.common.model.pay.*;
import com.jbp.common.response.pay.PayCreateResponse;
import com.jbp.common.response.pay.PayQueryResponse;
import com.jbp.common.response.pay.PayRefundResponse;

public interface PayAggregationMng {

    PayCreateResponse create(PayUser payUser, PayChannel payChannel, PaySubMerchant paySubMerchant, PayUnifiedOrder order);

    PayQueryResponse query(PayUser payUser, PayChannel payChannel, PaySubMerchant paySubMerchant, PayUnifiedOrder order);

    PayRefundResponse refund(PayChannel payChannel, PayUser payUser, PaySubMerchant subMerchant, PayUnifiedOrder payOrder, PayUnifiedRefundOrder refundOrder);

    PayRefundResponse refundQuery(PayChannel payChannel, PayUser payUser, PaySubMerchant subMerchant,
                                       PayUnifiedOrder payOrder, PayUnifiedRefundOrder refundOrder);
}
