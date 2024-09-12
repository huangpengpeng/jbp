package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ActivityScoreClearing;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.ActivityScoreClearingEditRequest;


public interface ActivityScoreClearingService extends IService<ActivityScoreClearing> {

    void clearingUser(Integer activityId);

    PageInfo<ActivityScoreClearing> getList(Integer uid, String activityScoreName, PageParamRequest pageParamRequest);

    Boolean del(Integer id);

    void verifyUser(Integer activityId);

    Boolean edit(ActivityScoreClearingEditRequest request);
}
