package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.user.User;
import com.jbp.common.model.user.UserLoginLog;
import com.jbp.service.dao.UserLoginLogDao;
import com.jbp.service.service.UserLoginLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class UserLoginLogServiceImpl extends ServiceImpl<UserLoginLogDao, UserLoginLog> implements UserLoginLogService {
    @Override
    public UserLoginLog add(User user, String type) {
        UserLoginLog loginLog = new UserLoginLog(user.getId(), user.getAccount(), user.getLastIp(), type);
        save(loginLog);
        return loginLog;
    }
}
