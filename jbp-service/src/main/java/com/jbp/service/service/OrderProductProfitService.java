package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.order.OrderProductProfit;

import java.util.List;

public interface OrderProductProfitService extends IService<OrderProductProfit> {

    OrderProductProfit save(Integer orderId, String orderNo, Integer productId, Integer profitType, String profitName, String rule);

    List<OrderProductProfit> getByOrder(Integer orderId, Integer profitType, String status);
}
