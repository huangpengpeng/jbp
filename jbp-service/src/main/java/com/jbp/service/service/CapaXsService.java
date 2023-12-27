package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.b2b.CapaXs;
import com.jbp.common.model.b2b.CapaXsCondition;

import java.util.List;

public interface CapaXsService extends IService<CapaXs> {

    CapaXs getMinCapa();

    CapaXs getNext(Long capaId);

    CapaXs getByName(String name);

    CapaXs getByRankNum(Integer rankNum);

    void saveOrUpdate(Long capaId, List<CapaXsCondition> conditionList, String parser);
}
