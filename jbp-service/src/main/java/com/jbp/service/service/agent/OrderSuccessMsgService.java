package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.OrderSuccessMsg;
import com.jbp.common.model.agent.OrdersRefundMsg;

public interface OrderSuccessMsgService extends IService<OrderSuccessMsg> {


   void  add(String orderSn);

    void exec(OrderSuccessMsg msg) throws InterruptedException;



}
