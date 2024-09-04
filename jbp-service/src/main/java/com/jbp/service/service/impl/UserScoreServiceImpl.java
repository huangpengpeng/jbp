package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserScore;
import com.jbp.common.request.UserScoreRequest;
import com.jbp.service.dao.UserScoreDao;
import com.jbp.service.service.SystemConfigService;
import com.jbp.service.service.UserScoreFlowService;
import com.jbp.service.service.UserScoreService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.CapaService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserInvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


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
    @Autowired
    private UserCapaService userCapaService;
    @Autowired
    private CapaService capaService;



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

        userScoreFlowService.add(uid, score, "增加", desc,"升级");

    }

    @Override
    public void reduce(Integer uid, Integer score, String desc,String remark) {
        UserScore userScore = dao.selectOne(new QueryWrapper<UserScore>().lambda().eq(UserScore::getUid, uid));
        if (userScore == null) {
            throw new RuntimeException("用户无分数，无法扣减");
        }

        if (userScore.getScore() - score < 0) {
            throw new RuntimeException("用户分数不足，无法扣减");
        }

        userScore.setScore(userScore.getScore() - score);
        dao.updateById(userScore);

        userScoreFlowService.add(uid, score, "减少", desc,remark);

    }

    @Override
    public void updateUserCapa(UserScoreRequest request) {


        List<User> phoneList = userService.getByPhone(request.getPhone());
        if(phoneList.isEmpty()){
            throw new CrmebException("手机号不存在");
        }
        if (phoneList.size() > 1) {
            throw new CrmebException("手机号重复");
        }


        //合伙人分数
        String reducePartnerMark = systemConfigService.getValueByKey("reduce_partner_mark");
        //事业合伙人
        String reduceCauseMark = systemConfigService.getValueByKey("reduce_cause_mark");
        Integer score = 0;
        if (request.getCapaId() == 3) {
            score = reducePartnerMark == null ? 0 : Integer.valueOf(reducePartnerMark);
        } else if (request.getCapaId() == 4) {
            score = reduceCauseMark == null ? 0 : Integer.valueOf(reduceCauseMark);
        }

        if (score > 0) {
            Capa capa =  capaService.getById(request.getCapaId());
            reduce(userService.getUserId(), score, "赠送"+capa.getName(),request.getPhone());
        }


        if (phoneList.isEmpty()) {
            //赠送用户等级账号
            User user = userService.registerNoBandPater(request.getPhone(), request.getPhone(), "上级赠送", request.getCapaId());
            //绑定关系
            userInvitationService.band(user.getId(), userService.getUserId(), false, true, false);
        }

        if(!phoneList.isEmpty()){
          UserCapa userCapa  =  userCapaService.getByUser(phoneList.get(0).getId());
          if(userCapa==null || userCapa.getCapaId() < request.getCapaId()){
              userCapaService.saveOrUpdateCapa(phoneList.get(0).getId(),request.getCapaId(),"赠送","上级赠送");
          }else{
              throw new CrmebException("赠送级别低于当前等级，无法操作");
          }
        }


    }


}

