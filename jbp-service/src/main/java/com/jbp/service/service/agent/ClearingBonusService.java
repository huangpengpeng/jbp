package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ClearingBonus;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.ClearingBonusListResponse;

import java.util.List;

public interface ClearingBonusService extends IService<ClearingBonus> {

    void insertBatchList(List<ClearingBonus> list);
    void del4Clearing(Long clearingId);

    List<ClearingBonus> get4Clearing(Long clearingId);

    PageInfo<ClearingBonus> pageList(Integer uid, String account, String uniqueNo, Long clearingId, PageParamRequest pageParamRequest);

    PageInfo<ClearingBonusListResponse> getClearingList(Integer uid, PageParamRequest pageParamRequest);


}
