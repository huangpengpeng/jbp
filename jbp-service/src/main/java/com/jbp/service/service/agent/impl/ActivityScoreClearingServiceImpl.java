package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.ActivityScore;
import com.jbp.common.model.agent.ActivityScoreClearing;
import com.jbp.common.model.agent.ActivityScoreGoods;
import com.jbp.common.model.agent.UserInvitation;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.dao.agent.ActivityScoreClearingDao;
import com.jbp.service.service.agent.ActivityScoreClearingService;
import com.jbp.service.service.agent.ActivityScoreGoodsService;
import com.jbp.service.service.agent.ActivityScoreService;
import com.jbp.service.service.agent.UserInvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class ActivityScoreClearingServiceImpl extends ServiceImpl<ActivityScoreClearingDao, ActivityScoreClearing> implements ActivityScoreClearingService {

    @Autowired
    private ActivityScoreService activityScoreService;
    @Autowired
    private ActivityScoreGoodsService activityScoreGoodsService;
    @Autowired
    private UserInvitationService userInvitationService;


    @Override
    public void clearingUser(Integer activityId) {
        ActivityScore activityScore = activityScoreService.getById(activityId);
        if (activityScore == null) {
            throw new RuntimeException("活动不存在");
        }


        ActivityScoreClearing activityScoreUser = getOne(new QueryWrapper<ActivityScoreClearing>().lambda().eq(ActivityScoreClearing::getActivityScoreId, activityId).last(" limit 1"));

        if (activityScoreUser != null) {
            throw new RuntimeException("活动已经结算，无法再次操作");
        }

        List<ActivityScoreGoods> activityScoreGoodsList = activityScoreGoodsService.list(new QueryWrapper<ActivityScoreGoods>().lambda().eq(ActivityScoreGoods::getActivityScoreId, activityId));
        List<Integer> productIds = activityScoreGoodsList.stream().map(ActivityScoreGoods::getActivityScoreGoodsId).collect(Collectors.toList());

        List<UserInvitation> userInvitationList = userInvitationService.list(new QueryWrapper<UserInvitation>().lambda().groupBy(UserInvitation::getPId));
        for (UserInvitation userInvitation : userInvitationList) {

            List<Integer> uids = userInvitationService.getNextPidList(userInvitation.getPId());
            //获取分值
            Integer score = activityScoreGoodsService.getProductNumber(productIds, uids, DateTimeUtils.format(activityScore.getStartTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN),
                    DateTimeUtils.format(activityScore.getEndTime(), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN), activityId);


            activityScore.getRule();


            ActivityScoreClearing activityScoreClearing = new ActivityScoreClearing();
            activityScoreClearing.setActivityScoreId(activityId);
//            activityScoreClearing.setCardCount(score);
//            activityScoreClearing.setScore(score);
            activityScoreClearing.setUid(userInvitation.getPId());
            activityScoreClearing.setStatus("待结算");
            save(activityScoreClearing);

        }
    }
}
