package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ActivityScore;
import com.jbp.common.model.agent.Capa;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.agent.ActivityScoreDao;
import com.jbp.service.service.agent.ActivityScoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class ActivityScoreServiceImpl extends ServiceImpl<ActivityScoreDao, ActivityScore> implements ActivityScoreService {


    @Override
    public PageInfo<ActivityScore> getList(PageParamRequest pageParamRequest) {
        Page<ActivityScore> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, list());
    }
}
