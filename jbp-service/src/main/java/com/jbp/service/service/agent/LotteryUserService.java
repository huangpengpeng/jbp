package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.LotteryUser;

public interface LotteryUserService extends IService<LotteryUser> {

    public LotteryUser reducere(Integer userId,Long lotteryId, Integer count);

    public LotteryUser increase(Integer userId,Long lotteryId, Integer count);



}
