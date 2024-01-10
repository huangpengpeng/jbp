package com.jbp.service.service.agent.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.ChannelCard;
import com.jbp.service.dao.agent.ChannelCardDao;
import com.jbp.service.service.agent.ChannelCardService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class ChannelCardServiceImpl extends ServiceImpl<ChannelCardDao, ChannelCard> implements ChannelCardService {


}
