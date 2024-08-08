package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.order.OrderFill;
import com.jbp.service.dao.OrderFillDao;
import com.jbp.service.service.OrderFillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class OrderFillServiceImpl extends ServiceImpl<OrderFillDao, OrderFill> implements OrderFillService {

    private static final Logger logger = LoggerFactory.getLogger(OrderFillService.class);

    @Resource
    private OrderFillDao dao;


}

