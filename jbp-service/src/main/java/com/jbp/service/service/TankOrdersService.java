package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.tank.TankOrders;

public interface TankOrdersService  extends IService<TankOrders> {

    TankOrders getOrderSn(String orderSn);


}
