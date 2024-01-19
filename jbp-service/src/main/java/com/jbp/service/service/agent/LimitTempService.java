package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LimitTemp;
import com.jbp.common.model.product.Product;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.LimitTempResponse;

import java.util.List;

public interface LimitTempService extends IService<LimitTemp> {

    LimitTemp add(String name, String type, List<Long> capaIdList, List<Long> capaXsIdList, List<Long> whiteIdList, List<Long> teamIdList, Boolean hasPartner, List<Long> pCapaIdList, List<Long> pCapaXsIdList, Boolean hasRelation, List<Long> rCapaIdList, List<Long> rCapaXsIdList, String description);

    void validBuy(Long capaId, Long capaXsId, List<Long> whiteIdList, List<Long> teamIdList, Integer pId, Integer rId, Product product);

    List<Long> hasLimits(Long capaId, Long capaXsId, List<Long> whiteIdList, List<Long> teamIdList, Integer pId, Integer rId);


    PageInfo<LimitTemp> pageList(String name, String type, PageParamRequest pageParamRequest);

    LimitTemp getByName(String name);

    void update(Long id, String name, String type, List<Long> capaIdList, List<Long> capaXsIdList, List<Long> whiteIdList, List<Long> teamIdList, Boolean hasPartner, List<Long> pCapaIdList, List<Long> pCapaXsIdList, Boolean hasRelation, List<Long> rCapaIdList, List<Long> rCapaXsIdList, String description);

    LimitTempResponse details(Integer id);
}
