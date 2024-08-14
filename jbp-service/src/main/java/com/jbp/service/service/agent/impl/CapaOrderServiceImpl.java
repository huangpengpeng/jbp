package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.CapaOrder;
import com.jbp.service.dao.agent.CapaOrderDao;
import com.jbp.service.service.agent.CapaOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class CapaOrderServiceImpl extends ServiceImpl<CapaOrderDao, CapaOrder> implements CapaOrderService {
    @Override
    public CapaOrder getByCapaId(Integer capaId) {
        return getOne(new QueryWrapper<CapaOrder>().lambda().eq(CapaOrder::getCapaId, capaId));
    }
}
