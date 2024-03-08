package com.jbp.service.product.profit;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.agent.ProductProfitService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购买商品收益处理链
 */
@Component
@Slf4j
public class ProductProfitChain implements ApplicationContextAware {

    @Resource
    private ProductProfitService productProfitService;
    @Resource
    private OrderDetailService orderDetailService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        final Map<String, ProductProfitHandler> beans = applicationContext.getBeansOfType(ProductProfitHandler.class);
        log.warn("ProductProfitChain:{}", JSONObject.toJSONString(beans));
        beans.values().stream().forEach(s -> handlers.put(s.getType(), s));
    }

    private Map<Integer, ProductProfitHandler> handlers = Maps.newConcurrentMap();

    /**
     * 保存
     */
    public void save(ProductProfit productProfit) {
        ProductProfitHandler handler = handlers.get(productProfit.getType());
        if (handler == null) {
            throw new CrmebException("当前商品收益类型不存在");
        }
        handler.save(productProfit);
    }

    /**
     * 订单支付成功
     */
    public void orderSuccess(Order order) {
        if (order.getUid() == null) {
            throw new CrmebException("下单注册用户请先完成用户注册");
        }
        List<OrderDetail> orderDetailList = orderDetailService.getByOrderNo(order.getOrderNo());
        List<Integer> productIdList = orderDetailList.stream().map(OrderDetail::getProductId).collect(Collectors.toList());
        List<ProductProfit> productProfitList = productProfitService.getByProduct(productIdList);
        handlers.forEach((k, v) -> {
            v.orderSuccess(order, orderDetailList, productProfitList);
        });
    }

    /**
     * 订单退款
     */
    public void orderRefund(Order order, RefundOrder refundOrder) {
        handlers.forEach((k, v) -> {
            v.orderRefund(order, refundOrder);
        });
    }
}
