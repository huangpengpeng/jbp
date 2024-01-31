package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.order.OrderExt;

public interface OrderExtService extends IService<OrderExt> {

    OrderExt getByOrder(String orderNo);
}
