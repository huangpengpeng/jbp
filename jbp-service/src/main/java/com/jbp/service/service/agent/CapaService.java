package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.RiseConditionRequest;

import java.util.List;
import java.util.Map;


public interface CapaService extends IService<Capa> {

    PageInfo<Capa> page(PageParamRequest pageParamRequest);

    Capa save(String name, Long pCapaId, int rankNum, String iconUrl, String riseImgUrl, String shareImgUrl);

    Capa saveRiseCondition(RiseConditionRequest request);
    Capa getMinCapa();

    Capa getNext(Long capaId);

    Capa getByName(String name);

    Capa getByRankNum(Integer rankNum);

    List<Capa> getList();
    Map<Long, Capa> getCapaMap();
}
