package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.LotteryUser;
import com.jbp.service.dao.agent.LotteryUserDao;
import com.jbp.service.service.agent.LotteryUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LotteryUserServiceImpl extends ServiceImpl<LotteryUserDao, LotteryUser> implements LotteryUserService {
    @Autowired
    private LotteryUserDao dao;
}
