package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.ClearingInvitationFlow;
import com.jbp.common.model.agent.ClearingRelationFlow;

import java.util.LinkedList;
import java.util.List;

public interface ClearingInvitationFlowService extends IService<ClearingInvitationFlow> {

    Boolean create(Long clearingId);

    Boolean del4Clearing(Long clearingId);

    List<ClearingInvitationFlow> getByUser(Integer uid, Integer limit);

    List<ClearingInvitationFlow> getByPUser(Integer pid);
}
