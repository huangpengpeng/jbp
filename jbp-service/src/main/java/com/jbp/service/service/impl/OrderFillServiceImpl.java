package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderFill;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.OrderFillDao;
import com.jbp.service.service.OrderFillService;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.agent.UserInvitationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;


@Service
public class OrderFillServiceImpl extends ServiceImpl<OrderFillDao, OrderFill> implements OrderFillService {

    private static final Logger logger = LoggerFactory.getLogger(OrderFillService.class);

    @Resource
    private OrderFillDao dao;
    @Resource
    private OrderService orderService;
    @Resource
    private UserInvitationService userInvitationService;


    @Override
    public OrderFill add(String orderNo, Integer uId) {

        OrderFill orderFill = new OrderFill();
        orderFill.setCreateTime(new Date());
        orderFill.setExpiredTime(DateTimeUtils.addHours(new Date(), 72));
        orderFill.setOrderNo(orderNo);
        orderFill.setStatus("待补单");
        orderFill.setUId(uId);
        save(orderFill);
        return orderFill;
    }

    @Override
    public OrderFill saveOrder(String orderNo) {

        Order order = orderService.getByOrderNo(orderNo);


        userInvitationService.getByUser(order.getUid());

        return null;
    }
}

