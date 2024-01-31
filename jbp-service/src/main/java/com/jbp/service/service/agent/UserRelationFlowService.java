package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.UserRelationFlow;
import com.jbp.common.request.PageParamRequest;

public interface UserRelationFlowService extends IService<UserRelationFlow> {

    void clear(Integer uid);

    void refresh(Integer uId);

    PageInfo<UserRelationFlow> pageList(Integer uid, Integer pid, Integer level, PageParamRequest pageParamRequest);
}
