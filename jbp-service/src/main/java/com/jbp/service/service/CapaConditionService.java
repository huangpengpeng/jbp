package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.b2b.CapaCondition;

import java.util.List;

public interface CapaConditionService extends IService<CapaCondition> {

    void deleteByCapa(Long capaId);

    List<CapaCondition> getByCapa(Long capaId);
}
