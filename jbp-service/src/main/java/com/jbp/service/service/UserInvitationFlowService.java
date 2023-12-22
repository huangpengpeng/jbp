package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.user.UserInvitationFlow;

import java.util.List;

public interface UserInvitationFlowService extends IService<UserInvitationFlow> {

    void deleteByUser(Long userId);

    void saveBatch(List<UserInvitationFlow> list);



}
