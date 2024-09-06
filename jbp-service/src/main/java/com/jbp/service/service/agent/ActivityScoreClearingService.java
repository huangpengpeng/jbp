package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ActivityScore;
import com.jbp.common.model.agent.ActivityScoreClearing;
import com.jbp.common.request.PageParamRequest;

public interface ActivityScoreClearingService extends IService<ActivityScoreClearing> {

    void clearingUser(Integer activityId);
}
