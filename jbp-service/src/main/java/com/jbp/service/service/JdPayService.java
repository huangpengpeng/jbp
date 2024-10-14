package com.jbp.service.service;

import com.jbp.common.jdpay.vo.*;

import java.math.BigDecimal;
import java.util.Date;

public interface JdPayService {


    JdPayAggregateCreateOrderResponse jdPay(String userId, String goodsName, String payCode, BigDecimal amt,
                                            String ip, Date createTime, String teamName);

    JdPayAggregateCreateOrderResponse aliPay(String userId, String goodsName, String payCode, BigDecimal amt,
                                             String ip, Date createTime, String teamName);

    JdPayQueryOrderResponse queryOrder(String payCode);

    JdPayRefundResponse refund(String payCode, String refundCode, BigDecimal amt, Date createTime);

    JdPaySendCommissionResponse sendCommission(String payCode, BigDecimal amt);

    JdPayToPersonalWalletResponse payToPersonalWallet(String merchantTradeNo,  String xid, BigDecimal amt, String merchantTradeDesc);

}
