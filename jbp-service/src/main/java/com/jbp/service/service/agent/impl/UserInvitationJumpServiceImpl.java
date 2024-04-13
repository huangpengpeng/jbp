package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.UserInvitationFlow;
import com.jbp.common.model.agent.UserInvitationJump;
import com.jbp.service.dao.agent.UserInvitationFlowDao;
import com.jbp.service.dao.agent.UserInvitationJumpDao;
import com.jbp.service.service.agent.UserInvitationFlowService;
import com.jbp.service.service.agent.UserInvitationJumpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 销售关系网跳转
 */
@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserInvitationJumpServiceImpl extends ServiceImpl<UserInvitationJumpDao, UserInvitationJump> implements UserInvitationJumpService {

    @Override
    public UserInvitationJump add(Integer uId, Integer pId, Integer orgPid) {
        UserInvitationJump jump = new UserInvitationJump(uId, pId, orgPid);
        save(jump);
        return jump;
    }

    @Override
    public Boolean ifJump(Integer uId) {
        return !list(new LambdaQueryWrapper<UserInvitationJump>().eq(UserInvitationJump::getUId, uId)).isEmpty();
    }
}
