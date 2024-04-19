package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.LztPayChannel;
import com.jbp.service.dao.agent.LztPayChannelDao;
import com.jbp.service.service.agent.LztPayChannelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LztPayChannelServiceImpl extends ServiceImpl<LztPayChannelDao, LztPayChannel> implements LztPayChannelService {
    @Override
    public LztPayChannel add(LztPayChannel lztPayChannel) {
        save(lztPayChannel);
        return lztPayChannel;
    }

    @Override
    public List<LztPayChannel> getByMer(Integer merId) {
        return list(new LambdaQueryWrapper<LztPayChannel>().eq(LztPayChannel::getMerId, merId));
    }
}
