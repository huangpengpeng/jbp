package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.b2b.Capa;
import com.jbp.common.model.b2b.CapaCondition;
import com.jbp.common.model.user.UserClosing;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.UserClosingSearchRequest;

import java.util.List;


public interface CapaService extends IService<Capa> {

    PageInfo<Capa> page(PageParamRequest pageParamRequest);

    Capa getMinCapa();

    Capa getNext(Long capaId);

    Capa getByName(String name);

    Capa getByRankNum(Integer rankNum);

    void saveOrUpdate(Long capaId, List<CapaCondition> conditionList, String parser);
}
