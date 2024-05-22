package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.agent.InvitationScoreGroup;
import com.jbp.common.request.PageParamRequest;

public interface InvitationScoreGroupService extends IService<InvitationScoreGroup> {
    PageInfo<InvitationScoreGroup> pageList(Integer uid, String groupName, String action, String nickname,PageParamRequest pageParamRequest);
}
