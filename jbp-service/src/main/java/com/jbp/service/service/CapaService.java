package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.b2b.Capa;
import com.jbp.common.model.b2b.CapaCondition;

import java.util.List;


public interface CapaService extends IService<Capa> {

    Capa getMinCapa();

    Capa getNext(Long capaId);

    Capa getByName(String name);

    Capa getByRankNum(Integer rankNum);

    void saveOrUpdate(Long capaId, List<CapaCondition> conditionList, String parser);
}
