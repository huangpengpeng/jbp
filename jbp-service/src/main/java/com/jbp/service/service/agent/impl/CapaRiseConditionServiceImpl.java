package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.service.dao.agent.CapaRiseConditionDao;
import com.jbp.service.service.agent.CapaRiseConditionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class CapaRiseConditionServiceImpl extends ServiceImpl<CapaRiseConditionDao, RiseCondition> implements CapaRiseConditionService {


    @Override
    public RiseCondition add(String name, String description, String value) {
        RiseCondition capaRiseCondition = new RiseCondition(name, description, value);
        save(capaRiseCondition);
        return capaRiseCondition;
    }
}
