package com.jbp.service.product.profit;

import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.product.ProductProfit;

import java.util.List;

public interface ProductProfitHandler {

    Integer getType();

    void save(ProductProfit productProfit);

    <T>T getRule(String rule);

    void orderSuccess(Order order, List<OrderDetail> orderDetailList, List<ProductProfit> productProfitList);

    void orderRefund(Order order);
}
