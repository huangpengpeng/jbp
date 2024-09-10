package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.Lottery;
import com.jbp.service.dao.agent.LotteryDao;
import com.jbp.service.service.agent.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LotteryServiceImpl extends ServiceImpl<LotteryDao, Lottery> implements LotteryService {

    @Autowired
    private LotteryDao dao;
}
