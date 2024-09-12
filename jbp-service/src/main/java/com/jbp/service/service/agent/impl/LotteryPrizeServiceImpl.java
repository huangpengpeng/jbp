package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.LotteryPrize;
import com.jbp.service.dao.agent.LotteryPrizeDao;
import com.jbp.service.service.agent.LotteryPrizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LotteryPrizeServiceImpl extends ServiceImpl<LotteryPrizeDao, LotteryPrize> implements LotteryPrizeService {
    @Autowired
    private LotteryPrizeDao dao;
}
