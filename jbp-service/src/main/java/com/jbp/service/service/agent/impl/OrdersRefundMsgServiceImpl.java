package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.OrdersRefundMsg;
import com.jbp.service.dao.agent.OrdersRefundMsgDao;
import com.jbp.service.service.agent.OrdersRefundMsgService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class OrdersRefundMsgServiceImpl extends ServiceImpl<OrdersRefundMsgDao, OrdersRefundMsg> implements OrdersRefundMsgService {
    @Override
    public OrdersRefundMsg create(String ordersSn, String refundSn, String context) {
        OrdersRefundMsg msg = new OrdersRefundMsg(ordersSn, refundSn, context);
        save(msg);
        return msg;
    }

    @Override
    public void read(List<Long> ids, String remark) {
        UpdateWrapper<OrdersRefundMsg> updateWrapper = new UpdateWrapper();
        updateWrapper.lambda().set(OrdersRefundMsg::getIfRead, true)
                .set(OrdersRefundMsg::getRemark, remark)
                .in(OrdersRefundMsg::getId, ids);
        update(updateWrapper);
    }
}
