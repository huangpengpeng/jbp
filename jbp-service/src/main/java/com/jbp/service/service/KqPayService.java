package com.jbp.service.service;

import com.jbp.common.kqbill.result.KqPayInfoResult;
import com.jbp.common.kqbill.result.KqPayQueryResult;
import com.jbp.common.kqbill.result.KqRefundQueryResult;
import com.jbp.common.kqbill.result.KqRefundResult;

import java.math.BigDecimal;
import java.util.Date;

public interface KqPayService {

    KqPayInfoResult get();

    String cashier(String payerId, String payerIP, String orderId, BigDecimal orderAmount, String productName, String pageUrl, Date orderTime);

    KqPayQueryResult queryPayResult(String orderId);

    KqRefundResult refund(String orderId, String refundId, BigDecimal amt, Date refundTime);

    KqRefundQueryResult queryRefundResult(String refundId, Date refundTime);

}
