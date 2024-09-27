package com.jbp.service.service;

import com.jbp.common.jdpay.vo.JdPayAggregateCreateOrderResponse;
import com.jbp.common.jdpay.vo.JdPayQueryOrderResponse;
import com.jbp.common.jdpay.vo.JdPayRefundResponse;

import java.math.BigDecimal;
import java.util.Date;

public interface JdPayService {


    JdPayAggregateCreateOrderResponse jdPay(String userId, String goodsName, String payCode, BigDecimal amt,
                                            String ip, Date createTime);

    JdPayAggregateCreateOrderResponse aliPay(String userId, String goodsName, String payCode, BigDecimal amt,
                                             String ip, Date createTime);

    JdPayQueryOrderResponse queryOrder(String payCode);

    JdPayRefundResponse refund(String payCode, String refundCode, BigDecimal amt, Date createTime);

}
