package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.LimitTemp;
import com.jbp.common.model.product.Product;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.LimitTempResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LimitTempService extends IService<LimitTemp> {

    LimitTemp add(String name, String type, List<Long> capaIdList, List<Long> capaXsIdList,
                  List<Long> whiteIdList, List<Long> teamIdList, Boolean hasPartner,
                  List<Long> pCapaIdList, List<Long> pCapaXsIdList, Boolean hasRelation,
                  List<Long> rCapaIdList, List<Long> rCapaXsIdList, Boolean hasBuyLimit,
                  int buyLimitNum, Date buyLimitStartTime, Date buyLimitEndTime, String description);

    List<Long> hasLimits(Long capaId, Long capaXsId, List<Long> whiteIdList, List<Long> teamIdList, Integer pId, Integer rId);

    void validBuy(Integer uid, Long capaId, Long capaXsId, List<Long> whiteIdList,
                         List<Long> teamIdList, Integer pId, Integer rId, List<Product> productList, Map<Integer, Integer> productNumMap);

    PageInfo<LimitTemp> pageList(String name, String type, PageParamRequest pageParamRequest);

    LimitTemp getByName(String name);

    void update(Long id, String name, String type, List<Long> capaIdList, List<Long> capaXsIdList,
                List<Long> whiteIdList, List<Long> teamIdList, Boolean hasPartner,
                List<Long> pCapaIdList, List<Long> pCapaXsIdList, Boolean hasRelation, List<Long> rCapaIdList,
                List<Long> rCapaXsIdList, Boolean hasBuyLimit,
                int buyLimitNum, Date buyLimitStartTime, Date buyLimitEndTime, String description);

    LimitTempResponse details(Integer id);


    List<LimitTemp> list(String type);
}
