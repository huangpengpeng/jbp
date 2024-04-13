package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.ClearingRelationFlow;
import com.jbp.common.model.agent.UserRelationFlow;
import com.jbp.common.request.PageParamRequest;

import java.util.List;


public interface ClearingRelationFlowService extends IService<ClearingRelationFlow> {

    Boolean create(Long clearingId);

    Boolean del4Clearing(Long clearingId);

    List<ClearingRelationFlow> getByUser(Integer uid, Integer limit);

    List<ClearingRelationFlow> getByPUser(Integer pid);

    PageInfo<ClearingRelationFlow> pageList(Integer uid, Integer pid, Long clearingId, Integer level, PageParamRequest pageParamRequest);


}
