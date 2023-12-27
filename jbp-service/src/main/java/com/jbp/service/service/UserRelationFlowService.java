package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.b2b.UserRelationFlow;

public interface UserRelationFlowService extends IService<UserRelationFlow> {

    void clear(Integer uid);

    void refresh(Integer uId);
}
