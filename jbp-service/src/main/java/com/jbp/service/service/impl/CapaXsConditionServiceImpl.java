package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.b2b.CapaXsCondition;
import com.jbp.service.dao.b2b.CapaXsConditionDao;
import com.jbp.service.service.CapaXsConditionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CapaXsConditionServiceImpl extends ServiceImpl<CapaXsConditionDao, CapaXsCondition> implements CapaXsConditionService {

    @Override
    public void deleteByCapa(Long capaId) {
        LambdaQueryWrapper<CapaXsCondition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CapaXsCondition::getCapaId, capaId);
        remove(wrapper);
    }

    @Override
    public List<CapaXsCondition> getByCapa(Long capaId) {
        LambdaQueryWrapper<CapaXsCondition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CapaXsCondition::getCapaId, capaId);
        return list(wrapper);
    }

}
