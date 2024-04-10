package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.tank.TankOrders;
import com.jbp.service.dao.TankOrdersDao;
import com.jbp.service.service.TankOrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Slf4j
@Service
public class TankOrdersServiceImpl extends ServiceImpl<TankOrdersDao, TankOrders> implements TankOrdersService {

    @Resource
    private TankOrdersDao dao;

    @Override
    public TankOrders getOrderSn(String orderSn) {
        return  dao.selectOne(new QueryWrapper<TankOrders>().lambda().eq(TankOrders::getOrderSn,orderSn));
    }


}
