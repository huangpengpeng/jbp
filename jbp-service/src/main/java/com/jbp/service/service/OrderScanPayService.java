package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.order.OrderScanPay;
import com.jbp.common.request.OrderScanPayRequest;

public interface OrderScanPayService extends IService<OrderScanPay> {
    OrderScanPay create(OrderScanPayRequest request);
}
