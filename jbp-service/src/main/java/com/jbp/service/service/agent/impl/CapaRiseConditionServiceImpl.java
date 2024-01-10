package com.jbp.service.service.agent.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.CapaRiseCondition;
import com.jbp.service.dao.agent.CapaRiseConditionDao;
import com.jbp.service.service.agent.CapaRiseConditionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class CapaRiseConditionServiceImpl extends ServiceImpl<CapaRiseConditionDao, CapaRiseCondition> implements CapaRiseConditionService {

    @Override
    public List<CapaRiseCondition> getByType(Integer type) {
        LambdaQueryWrapper<CapaRiseCondition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CapaRiseCondition::getType, type);
        return list(wrapper);
    }

    @Override
    public CapaRiseCondition add(Integer type, String name, String description, JSONObject value) {
        CapaRiseCondition capaRiseCondition = new CapaRiseCondition(type, name, description, value);
        save(capaRiseCondition);
        return capaRiseCondition;
    }
}
