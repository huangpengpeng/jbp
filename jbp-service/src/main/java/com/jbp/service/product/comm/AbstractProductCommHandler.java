package com.jbp.service.product.comm;

import com.jbp.common.model.agent.ClearingFinal;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.order.Order;
import com.jbp.service.service.agent.ProductCommConfigService;
import com.jbp.service.service.agent.ProductCommService;

import javax.annotation.Resource;
import java.util.LinkedList;

public abstract class AbstractProductCommHandler implements ProductCommHandler {

    @Override
    public Integer order() {
        return 0;
    }

    @Override
    public <T> T getRule(ProductComm productComm) {
        return null;
    }

    @Override
    public void orderSuccessCalculateAmt(Order order, LinkedList<CommCalculateResult> resultList) {

    }

    @Override
    public void clearing(ClearingFinal clearingFinal) {

    }
}
