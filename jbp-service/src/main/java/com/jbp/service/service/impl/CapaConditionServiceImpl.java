package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.b2b.CapaCondition;
import com.jbp.service.dao.b2b.CapaConditionDao;
import com.jbp.service.service.CapaConditionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CapaConditionServiceImpl extends ServiceImpl<CapaConditionDao, CapaCondition> implements CapaConditionService {
    @Override
    public void deleteByCapa(Long capaId) {
        LambdaQueryWrapper<CapaCondition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CapaCondition::getCapaId, capaId);
        remove(wrapper);
    }

    @Override
    public List<CapaCondition> getByCapa(Long capaId) {
        LambdaQueryWrapper<CapaCondition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CapaCondition::getCapaId, capaId);
        return list(wrapper);
    }


}
