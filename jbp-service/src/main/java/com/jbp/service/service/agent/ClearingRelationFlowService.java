package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.ClearingRelationFlow;

import java.util.List;


public interface ClearingRelationFlowService extends IService<ClearingRelationFlow> {

    Boolean create(Long clearingId);

    Boolean del4Clearing(Long clearingId);

    List<ClearingRelationFlow> getByUser(Integer uid, Integer limit);

    List<ClearingRelationFlow> getByPUser(Integer pid);


}
