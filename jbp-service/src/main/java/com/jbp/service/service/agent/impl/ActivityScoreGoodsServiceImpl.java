package com.jbp.service.service.agent.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ActivityScore;
import com.jbp.common.model.agent.ActivityScoreGoods;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ActivityScoreGoodsAddRequest;
import com.jbp.common.request.agent.ActivityScoreGoodsEditRequest;
import com.jbp.common.request.agent.ActivityScoreGoodsRequest;
import com.jbp.common.request.agent.ActivityScoreGoodsSearchRequest;
import com.jbp.service.dao.agent.ActivityScoreGoodsDao;
import com.jbp.service.service.agent.ActivityScoreGoodsService;
import com.jbp.service.service.agent.ActivityScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class ActivityScoreGoodsServiceImpl extends ServiceImpl<ActivityScoreGoodsDao,ActivityScoreGoods> implements ActivityScoreGoodsService {

    @Autowired
    private ActivityScoreGoodsDao dao;
    @Autowired
    private ActivityScoreService activityScoreService;

    @Override
    public PageInfo<ActivityScoreGoods> getList(ActivityScoreGoodsSearchRequest request, PageParamRequest pageParamRequest) {
        Page<ActivityScoreGoods> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ActivityScoreGoods> list = dao.getList(request.getActivityScoreId());
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Boolean add(ActivityScoreGoodsAddRequest request) {
        ActivityScore activityScore = activityScoreService.getById(request.getActivityScoreId());
        if (activityScore == null) {
            throw new CrmebException("活动不存在");
        }
        if (request.getActivityScoreGoodsList().isEmpty()){
            throw new CrmebException("活动商品不能为空");
        }
        List<ActivityScoreGoods> list = list(new QueryWrapper<ActivityScoreGoods>().lambda().eq(ActivityScoreGoods::getActivityScoreId, request.getActivityScoreId()));
        List<Integer> productIds = CollUtil.newArrayList();
        if (!list.isEmpty()){
            productIds = list.stream().map(ActivityScoreGoods::getActivityScoreGoodsId).collect(Collectors.toList());
        }
        for (ActivityScoreGoodsRequest activityGood : request.getActivityScoreGoodsList()) {
            if (productIds.contains(activityGood.getProductId())){
                continue;
            }
            ActivityScoreGoods goods = new ActivityScoreGoods();
            goods.setActivityScoreId(request.getActivityScoreId());
            goods.setActivityScoreGoodsId(activityGood.getProductId());
            goods.setGoodsCount(activityGood.getGoodsCount());
            save(goods);
        }
        return true;
    }

    @Override
    public Boolean edit(ActivityScoreGoodsEditRequest request) {
        ActivityScoreGoods activityScoreGoods = getById(request.getId());
        if (activityScoreGoods == null) {
            throw new CrmebException("活动不存在！");
        }
        activityScoreGoods.setGoodsCount(request.getGoodsCount());
        return updateById(activityScoreGoods);
    }
    @Override
    public Integer getProductNumber(List<Integer> productId, List<Integer> uid,String startTime,String endTime,Integer activityId) {
        return dao.getProductNumber(productId,uid,startTime,endTime,activityId);
    }
}
