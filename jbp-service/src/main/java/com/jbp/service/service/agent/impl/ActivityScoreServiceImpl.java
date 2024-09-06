package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ActivityScore;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ActivityScoreAddRequest;
import com.jbp.common.request.agent.ActivityScoreEditRequest;
import com.jbp.service.dao.agent.ActivityScoreDao;
import com.jbp.service.service.SystemAttachmentService;
import com.jbp.service.service.agent.ActivityScoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class ActivityScoreServiceImpl extends ServiceImpl<ActivityScoreDao, ActivityScore> implements ActivityScoreService {


    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Override
    public PageInfo<ActivityScore> getList(PageParamRequest pageParamRequest) {
        Page<ActivityScore> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, list(new QueryWrapper<ActivityScore>().lambda().orderByDesc(ActivityScore::getId)));
    }

    @Override
    public Integer add(ActivityScoreAddRequest request) {
        ActivityScore activityScore = new ActivityScore();
        BeanUtils.copyProperties(request,activityScore);
        String cdnUrl = systemAttachmentService.getCdnUrl();
        activityScore.setMark(systemAttachmentService.clearPrefix(activityScore.getMark(), cdnUrl));
        save(activityScore);
        return activityScore.getId().intValue();
    }

    @Override
    public Boolean edit(ActivityScoreEditRequest request) {
        ActivityScore activityScore = getById(request.getId());
        if (activityScore == null) {
            throw new CrmebException("id不存在！");
        }
        BeanUtils.copyProperties(request,activityScore);
        return updateById(activityScore);
    }


}
