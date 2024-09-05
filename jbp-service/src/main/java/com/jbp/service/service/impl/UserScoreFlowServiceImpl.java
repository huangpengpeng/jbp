package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.user.UserScoreFlow;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.UserScoreFlowDao;
import com.jbp.service.service.UserScoreFlowService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@Service
public class UserScoreFlowServiceImpl extends ServiceImpl<UserScoreFlowDao, UserScoreFlow> implements UserScoreFlowService {

    @Resource
    private UserScoreFlowDao dao;


    @Override
    public void add(Integer uid, Integer score, String type, String desc, String remark, Integer surplusScore) {

        UserScoreFlow userScoreFlow = new UserScoreFlow();
        userScoreFlow.setUid(uid);
        userScoreFlow.setType(type);
        userScoreFlow.setScore(score);
        userScoreFlow.setCreateTime(new Date());
        userScoreFlow.setDescription(desc);
        userScoreFlow.setRemark(remark);
        userScoreFlow.setSurplusScore(surplusScore);
        dao.insert(userScoreFlow);
    }

    @Override
    public PageInfo<UserScoreFlow> getList(Integer uid, PageParamRequest pageParamRequest) {
        Page<UserScoreFlow> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        List<UserScoreFlow> list = dao.getList(uid);
        return CommonPage.copyPageInfo(page, list);
    }
}

