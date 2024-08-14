package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.order.OrderFill;

public interface OrderFillService extends IService<OrderFill> {


    OrderFill add(String orderNo,Integer uId);



    OrderFill saveOrder(String orderNo);

}
