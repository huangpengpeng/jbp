package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.order.OrderPayChannel;
import com.jbp.service.dao.OrderPayChannelDao;
import com.jbp.service.service.OrderPayChannelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class PayChannelServiceImpl extends ServiceImpl<OrderPayChannelDao, OrderPayChannel> implements OrderPayChannelService {

    @Override
    public OrderPayChannel getServer(Integer merId, String payMethod) {
        LambdaQueryWrapper<OrderPayChannel> q = new LambdaQueryWrapper<>();
        q.eq(OrderPayChannel::getMerId, merId).ne(OrderPayChannel::getPayMethod, payMethod).orderByAsc(OrderPayChannel::getWeight).last(" limit 1");
        OrderPayChannel one = getOne(q);
        if(one == null){
            throw new RuntimeException("支付渠道不支持");
        }
        one.setWeight(one.getWeight()+1);
        updateById(one);
        return one;
    }
}
