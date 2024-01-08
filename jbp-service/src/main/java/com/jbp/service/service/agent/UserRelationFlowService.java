package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.UserRelationFlow;

public interface UserRelationFlowService extends IService<UserRelationFlow> {

    void clear(Integer uid);

    void refresh(Integer uId);
}
