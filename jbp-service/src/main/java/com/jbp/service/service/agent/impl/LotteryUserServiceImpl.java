package com.jbp.service.service.agent.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jbp.common.model.agent.LotteryUser;
import com.jbp.service.dao.agent.LotteryUserDao;
import com.jbp.service.service.agent.LotteryUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.REPEATABLE_READ)
@Service
public class LotteryUserServiceImpl extends ServiceImpl<LotteryUserDao, LotteryUser> implements LotteryUserService {


    @Override
    public LotteryUser reducere(Integer userId, Long lotteryId, Integer count) {
        LotteryUser lotteryUser = getOne(new QueryWrapper<LotteryUser>().lambda().eq(LotteryUser::getLotteryId, lotteryId).eq(LotteryUser::getUid, userId));

        if (lotteryUser == null || lotteryUser.getNumber() - count < 0) {
            throw new RuntimeException("抽奖次数不足");
        }

        lotteryUser.setNumber(lotteryUser.getNumber() - count);
        updateById(lotteryUser);
        return lotteryUser;
    }


    @Override
    public LotteryUser increase(Integer userId, Long lotteryId, Integer count) {
        LotteryUser lotteryUser = getOne(new QueryWrapper<LotteryUser>().lambda().eq(LotteryUser::getLotteryId, lotteryId).eq(LotteryUser::getUid, userId));

        if (lotteryUser == null) {
            lotteryUser = new LotteryUser();
            lotteryUser.setNumber(count);
            lotteryUser.setUid(userId);
            lotteryUser.setLotteryId(lotteryId);
            save(lotteryUser);
            return lotteryUser;
        } else {
            lotteryUser.setNumber(lotteryUser.getNumber() + count);
            updateById(lotteryUser);
            return lotteryUser;
        }
    }
}
