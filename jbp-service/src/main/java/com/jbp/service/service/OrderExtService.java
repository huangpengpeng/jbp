package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.order.OrderExt;
import com.jbp.common.model.user.User;

import java.util.List;
import java.util.Map;

public interface OrderExtService extends IService<OrderExt> {

    OrderExt getByOrder(String orderNo);

    Map<String, OrderExt> getOrderNoMapList(List<String> orderNoList);
}
