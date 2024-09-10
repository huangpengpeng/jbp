package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ActivityScore;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ActivityScoreAddRequest;
import com.jbp.common.request.agent.ActivityScoreEditRequest;

import java.util.List;

public interface ActivityScoreService extends IService<ActivityScore> {
    PageInfo<ActivityScore> getList(PageParamRequest pageParamRequest);

    Integer add(ActivityScoreAddRequest request);

    Boolean edit(ActivityScoreEditRequest request);

    List<ActivityScore> tree();
}
