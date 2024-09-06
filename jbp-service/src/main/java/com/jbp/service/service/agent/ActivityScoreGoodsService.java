package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ActivityScoreGoods;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ActivityScoreGoodsAddRequest;
import com.jbp.common.request.agent.ActivityScoreGoodsEditRequest;
import com.jbp.common.request.agent.ActivityScoreGoodsSearchRequest;

import java.util.List;


public interface ActivityScoreGoodsService extends IService<ActivityScoreGoods> {

    public Integer getProductNumber(List<Integer> productId, List<Integer> uid,String startTime,String endTime,Integer activityId);

    PageInfo<ActivityScoreGoods> getList(ActivityScoreGoodsSearchRequest request, PageParamRequest pageParamRequest);

    Boolean add(ActivityScoreGoodsAddRequest request);

    Boolean edit(ActivityScoreGoodsEditRequest request);
}
