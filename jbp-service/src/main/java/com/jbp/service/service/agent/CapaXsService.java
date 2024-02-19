package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.CapaXs;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.RiseConditionRequest;

public interface CapaXsService extends IService<CapaXs> {

    PageInfo<CapaXs> page(PageParamRequest pageParamRequest);

    CapaXs save(String name, Long pCapaId, int rankNum, String iconUrl, String riseImgUrl, String shareImgUrl);

    CapaXs saveRiseCondition(RiseConditionRequest request);

    CapaXs getMinCapa();

    CapaXs getNext(Long capaId);

    CapaXs getByName(String name);

    CapaXs getByRankNum(Integer rankNum);
}
