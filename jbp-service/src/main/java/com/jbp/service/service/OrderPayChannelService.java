package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.order.OrderPayChannel;

public interface OrderPayChannelService extends IService<OrderPayChannel> {

    OrderPayChannel getServer(String payMethod);
}
