package com.jbp.service.service.agent.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.*;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ActivityScoreClearingEditRequest;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.agent.ActivityScoreClearingDao;
import com.jbp.service.service.agent.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class ActivityScoreClearingServiceImpl extends ServiceImpl<ActivityScoreClearingDao, ActivityScoreClearing> implements ActivityScoreClearingService {

    @Autowired
    private ActivityScoreClearingDao dao;
    @Autowired
    private ActivityScoreService activityScoreService;
    @Autowired
    private ActivityScoreGoodsService activityScoreGoodsService;
    @Autowired
    private UserInvitationService userInvitationService;
    @Autowired
    private UserCapaService userCapaService;
    @Autowired
    private WalletService walletService;


    @Override
    public void clearingUser(Integer activityId) {
        ActivityScore activityScore = activityScoreService.getById(activityId);
        if (activityScore == null) {
            throw new RuntimeException("活动不存在");
        }


        //      ActivityScoreClearing activityScoreUser = getOne(new QueryWrapper<ActivityScoreClearing>().lambda().eq(ActivityScoreClearing::getActivityScoreId, activityId).last(" limit 1"));

//        if (activityScoreUser != null) {
//            throw new RuntimeException("活动已经结算，无法再次操作");
//        }

        List<ActivityScoreGoods> activityScoreGoodsList = activityScoreGoodsService.list(new QueryWrapper<ActivityScoreGoods>().lambda().eq(ActivityScoreGoods::getActivityScoreId, activityId));
        List<Integer> productIds = activityScoreGoodsList.stream().map(ActivityScoreGoods::getActivityScoreGoodsId).collect(Collectors.toList());

        List<UserInvitation> userInvitationList = userInvitationService.list(new QueryWrapper<UserInvitation>().lambda().groupBy(UserInvitation::getPId));
        int j = 0;
        for (UserInvitation userInvitation : userInvitationList) {
            j++;
            log.info("总数:{},当前条数:{}", userInvitationList.size(), j);
            UserCapa capa = userCapaService.getByUser(userInvitation.getPId());
            if (capa == null || capa.getCapaId() < activityScore.getCapaId()) {
                continue;
            }


            ActivityScoreClearing activityScoreClearing = new ActivityScoreClearing();
            activityScoreClearing.setCardCount(0);
            activityScoreClearing.setScore(0);
            List<Integer> uids = userInvitationService.getNextPidList(userInvitation.getPId());
            //获取分值
            BigDecimal score = activityScoreGoodsService.getProductNumber(productIds, uids, DateTimeUtils.format(activityScore.getStartTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN),
                    DateTimeUtils.format(activityScore.getEndTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN), activityId);

            //计算分值能获得什么奖励
            JSONArray jsonArray = JSONArray.parseArray(activityScore.getRule());
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (score.compareTo(jsonObject.getBigDecimal("score")) >= 0) {
                    activityScoreClearing.setCardCount(jsonObject.getInteger("number"));
                    activityScoreClearing.setScore(jsonObject.getInteger("bonuspoints"));
                }
            }

            if (activityScoreClearing.getCardCount() <= 0 && activityScoreClearing.getScore() <= 0) {
                continue;
            }
            activityScoreClearing.setActivityScoreId(activityId);
            activityScoreClearing.setUid(userInvitation.getPId());
            activityScoreClearing.setStatus("待结算");
            save(activityScoreClearing);

        }
    }

    @Override
    public void verifyUser(Integer activityId) {
        ActivityScore activityScore = activityScoreService.getById(activityId);
        List<ActivityScoreClearing> activityScoreClearings = list(new QueryWrapper<ActivityScoreClearing>().lambda().eq(ActivityScoreClearing::getActivityScoreId, activityId));
        if (!activityScoreClearings.get(0).getStatus().equals("待结算")) {
            throw new RuntimeException("活动已经结算，无法再次操作");
        }

        for (ActivityScoreClearing activityScoreClearing : activityScoreClearings) {

            if (activityScoreClearing.getScore() > 0) {
                walletService.increase(activityScoreClearing.getUid(), 3, new BigDecimal(activityScoreClearing.getScore()), WalletFlow.OperateEnum.奖励.name(), "", activityScore.getName() + "获得");
            }
            activityScoreClearing.setStatus("已结算");
            activityScoreClearing.setClearTime(new Date());
            updateById(activityScoreClearing);

        }

    }

    @Override
    public Boolean edit(ActivityScoreClearingEditRequest request) {
        ActivityScoreClearing scoreClearing = getById(request.getId());
        if (scoreClearing == null) {
            throw new CrmebException("积分活动不存在！");
        }
        BeanUtils.copyProperties(request, scoreClearing);
        return updateById(scoreClearing);
    }


    @Override
    public PageInfo<ActivityScoreClearing> getList(Integer uid, String activityScoreName, PageParamRequest pageParamRequest) {
        Page<ActivityScoreClearing> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<ActivityScoreClearing> list = dao.getList(uid, activityScoreName);
        return CommonPage.copyPageInfo(page, list);
    }

    @Override
    public Boolean del(Integer activityId) {
        ActivityScore activityScore = activityScoreService.getById(activityId);
        if (activityScore == null) {
            throw new RuntimeException("活动不存在");
        }
        ActivityScoreClearing activityScoreUser = getOne(new QueryWrapper<ActivityScoreClearing>().lambda().eq(ActivityScoreClearing::getActivityScoreId, activityId).eq(ActivityScoreClearing::getStatus, "已结算").last(" limit 1"));
        if (activityScoreUser != null) {
            throw new RuntimeException("活动已结算，无法删除！");
        }
        return remove(new QueryWrapper<ActivityScoreClearing>().lambda().eq(ActivityScoreClearing::getActivityScoreId, activityId));
    }
}

