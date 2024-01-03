package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.order.OrderExt;
import com.jbp.service.dao.OrderExtDao;
import com.jbp.service.service.OrderExtService;
import org.springframework.stereotype.Service;

@Service
public class OrderExtServiceImpl extends ServiceImpl<OrderExtDao, OrderExt> implements OrderExtService {
    @Override
    public OrderExt getByOrder(Integer orderId) {
        return getOne(new QueryWrapper<OrderExt>().lambda().eq(OrderExt::getOrderId, orderId));
    }
}
