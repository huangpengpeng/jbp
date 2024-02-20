package com.jbp.service.product.comm;

import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;

import java.math.BigDecimal;
import java.util.LinkedList;

public interface ProductCommHandler {

    BigDecimal getWalletDeductionListPv(OrderDetail orderDetail);

    Integer getType();

    /**
     * 执行顺序 越小优先级越高
     * @return
     */
    Integer order();

    Boolean saveOrUpdate(ProductComm productComm);

    <T>T getRule(ProductComm productComm);

    /**
     * 订单支付成功计算佣金
     * @param order  订单信息
     * @param resultList  佣金集合【佣金存在依赖关系所以计算结果要在各个佣金中流转】
     */
     void orderSuccessCalculateAmt(Order order, LinkedList<CommCalculateResult> resultList);
}
