package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.dto.RewardContextDTO;
import com.jbp.common.model.agent.Lottery;

public interface LotteryService extends IService<Lottery> {



    public RewardContextDTO doDraw(String ip, Long id) throws Exception;
}
