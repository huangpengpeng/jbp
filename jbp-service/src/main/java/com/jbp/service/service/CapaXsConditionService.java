package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.b2b.CapaXsCondition;

import java.util.List;

public interface CapaXsConditionService extends IService<CapaXsCondition> {

    void deleteByCapa(Long capaId);

    List<CapaXsCondition> getByCapa(Long capaId);
}
