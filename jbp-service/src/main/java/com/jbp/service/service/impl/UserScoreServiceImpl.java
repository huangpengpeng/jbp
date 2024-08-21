package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserScore;
import com.jbp.common.request.UserScoreRequest;
import com.jbp.service.dao.UserScoreDao;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserScoreFlowService;
import com.jbp.service.service.UserScoreService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.UserInvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class UserScoreServiceImpl extends ServiceImpl<UserScoreDao, UserScore> implements UserScoreService {

    @Resource
    private UserScoreDao dao;
    @Resource
    private UserScoreFlowService userScoreFlowService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserInvitationService userInvitationService;

    @Autowired
    private SystemConfigService systemConfigService;


    @Override
    public void increase(Integer uid, Integer score, String desc) {

        UserScore userScore = dao.selectOne(new QueryWrapper<UserScore>().lambda().eq(UserScore::getUid, uid));

        if (userScore == null) {
            userScore = new UserScore();
            userScore.setScore(score);
            userScore.setUid(uid);
            dao.insert(userScore);
        }

        userScore.setScore(userScore.getScore() + score);
        dao.updateById(userScore);

        userScoreFlowService.add(uid, score, "增加", desc);

    }

    @Override
    public void reduce(Integer uid, Integer score, String desc) {
        UserScore userScore = dao.selectOne(new QueryWrapper<UserScore>().lambda().eq(UserScore::getUid, uid));
        if (userScore == null) {
            throw new RuntimeException("用户无分数，无法扣减");
        }

        if (userScore.getScore() - score < 0) {
            throw new RuntimeException("用户分数不足，无法扣减");
        }

        userScore.setScore(userScore.getScore() - score);
        dao.updateById(userScore);

        userScoreFlowService.add(uid, score, "减少", desc);

    }

    @Override
    public void updateUserCapa(UserScoreRequest request) {


        //赠送用户等级账号
        User user = userService.registerNoBandPater(request.getPhone(), request.getPhone(), "上级赠送", request.getCapaId());

        //绑定关系
        userInvitationService.band(user.getId(), userService.getUserId(), false, true, false);

        //合伙人分数
        String reducePartnerMark = systemConfigService.getValueByKey("reduce_partner_mark");
        //事业合伙人
        String reduceCauseMark = systemConfigService.getValueByKey("reduce_cause_mark");
        Integer score = 0;
        if (request.getCapaId() == 2) {
            score = reducePartnerMark == null ? 0 : Integer.valueOf(reducePartnerMark);
        } else if (request.getCapaId() == 3) {
            score = reduceCauseMark == null ? 0 : Integer.valueOf(reduceCauseMark);
        }

        if (score > 0) {
            reduce(userService.getUserId(), score, "赠送等级");
        }
    }


}

