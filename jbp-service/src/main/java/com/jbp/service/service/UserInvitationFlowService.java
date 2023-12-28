package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.UserInvitationFlow;

public interface UserInvitationFlowService extends IService<UserInvitationFlow> {



    /**
     * 用户关系如果变更需要将更当前用户有关的的记录全部删除重新生成
     */
    void clear(Integer uId);

    /**
     * 刷新用户关系
     */
    void refreshFlowAndTeam(Integer uId);
}
