package com.jbp.service.service.pay;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.pay.PayUnifiedRefundOrder;
import com.jbp.common.request.pay.PayRefundQueryRequest;
import com.jbp.common.request.pay.PayRefundRequest;
import com.jbp.common.response.pay.PayRefundResponse;

public interface PayUnifiedRefundOrderMng extends IService<PayUnifiedRefundOrder> {
    PayRefundResponse refund(PayRefundRequest request);

    PayRefundResponse refundQuery(PayRefundQueryRequest request);

    PayUnifiedRefundOrder refresh(PayUnifiedRefundOrder order);
}
