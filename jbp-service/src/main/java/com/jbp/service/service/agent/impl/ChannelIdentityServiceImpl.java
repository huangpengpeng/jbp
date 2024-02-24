package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.ChannelIdentity;
import com.jbp.service.dao.agent.ChannelIdentityDao;
import com.jbp.service.service.agent.ChannelIdentityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ChannelIdentityServiceImpl extends ServiceImpl<ChannelIdentityDao, ChannelIdentity> implements ChannelIdentityService {
}
