package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.user.UserScoreFlow;
import com.jbp.service.dao.UserScoreFlowDao;
import com.jbp.service.service.UserScoreFlowService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;


@Service
public class UserScoreFlowServiceImpl extends ServiceImpl<UserScoreFlowDao, UserScoreFlow> implements UserScoreFlowService {

    @Resource
    private UserScoreFlowDao dao;


    @Override
    public void add(Integer uid, Integer score, String type, String desc,String remark) {

        UserScoreFlow userScoreFlow = new UserScoreFlow();
        userScoreFlow.setUid(uid);
        userScoreFlow.setType(type);
        userScoreFlow.setScore(score);
        userScoreFlow.setCreateTime(new Date());
        userScoreFlow.setDescription(desc);
        userScoreFlow.setRemark(remark);
        dao.insert(userScoreFlow);
    }
}

