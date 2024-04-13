package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.UserInvitationJump;

public interface UserInvitationJumpService extends IService<UserInvitationJump> {

    UserInvitationJump add(Integer uId, Integer pId, Integer orgPid);

    Boolean ifJump(Integer uId);
}
