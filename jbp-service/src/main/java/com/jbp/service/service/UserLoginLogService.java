package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserLoginLog;

public interface UserLoginLogService extends IService<UserLoginLog> {

    UserLoginLog add(User user, String type);
}
