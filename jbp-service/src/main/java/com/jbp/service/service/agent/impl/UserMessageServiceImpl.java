package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.UserMessage;
import com.jbp.service.dao.agent.UserMessageDao;
import com.jbp.service.service.UserMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class UserMessageServiceImpl extends ServiceImpl<UserMessageDao, UserMessage> implements UserMessageService {
}
