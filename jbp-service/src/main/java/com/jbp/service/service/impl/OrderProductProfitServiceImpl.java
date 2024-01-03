package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.order.OrderProductProfit;
import com.jbp.service.dao.OrderProductProfitDao;
import com.jbp.service.service.OrderProductProfitService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderProductProfitServiceImpl extends ServiceImpl<OrderProductProfitDao, OrderProductProfit> implements OrderProductProfitService {
    @Override
    public OrderProductProfit save(Integer orderId, String orderNo, Integer productId, Integer profitType, String profitName, String rule) {
        OrderProductProfit profit = new OrderProductProfit(orderId, orderNo, productId, profitType, profitName, rule);
        save(profit);
        return profit;
    }

    @Override
    public List<OrderProductProfit> getByOrder(Integer orderId, Integer profitType, String status) {
        return list(new QueryWrapper<OrderProductProfit>().lambda()
                .eq(OrderProductProfit::getOrderId, orderId)
                .eq(OrderProductProfit::getProfitType, profitType)
                .eq(OrderProductProfit::getStatus, status));
    }
}
