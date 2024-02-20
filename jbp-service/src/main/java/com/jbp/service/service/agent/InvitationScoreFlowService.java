package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.InvitationScoreFlow;
import com.jbp.common.request.PageParamRequest;

public interface InvitationScoreFlowService extends IService<InvitationScoreFlow> {
     PageInfo<InvitationScoreFlow> pageList(Integer uid, Integer orderuid, String action, PageParamRequest pageParamRequest);
}
