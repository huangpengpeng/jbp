package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ClearingBonus;
import com.jbp.common.model.agent.ClearingBonusFlow;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.ClearingBonusListResponse;

import java.util.List;

public interface ClearingBonusFlowService extends IService<ClearingBonusFlow> {

    void insertBatchList(List<ClearingBonusFlow> list);

    void del4Clearing(Long clearingId);

    PageInfo<ClearingBonusFlow> pageList(Integer uid, String account, Long clearingId, PageParamRequest pageParamRequest);

    List<ClearingBonusFlow> getByClearing(Long clearingId);

    List<ClearingBonusListResponse> getClearingList(Integer userId, String day);
}
