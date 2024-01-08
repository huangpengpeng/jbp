package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.request.PageParamRequest;


public interface CapaService extends IService<Capa> {

    PageInfo<Capa> page(PageParamRequest pageParamRequest);

    Capa save(String name, Long pCapaId, int rankNum, String iconUrl, String riseImgUrl, String shareImgUrl);

    Capa getMinCapa();

    Capa getNext(Long capaId);

    Capa getByName(String name);

    Capa getByRankNum(Integer rankNum);
}
