package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.order.OrderPayRecord;

import java.math.BigDecimal;

public interface OrderPayRecordService extends IService<OrderPayRecord> {

    OrderPayRecord getByOrderNo(String orderNo);

    OrderPayRecord scanPay(String payeeName, Integer merId, String payMethod, BigDecimal payPrice, String remark);

    OrderPayRecord create(String type, String orderNo, String payMethod, String payType, String payChannel, String merchantName,
                          String merchantNo, BigDecimal payPrice, String payTime);

    void refund(String orderNo, BigDecimal refundPrice);
}
