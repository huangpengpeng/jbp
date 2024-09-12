package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.LotteryPrize;
import com.jbp.common.request.agent.LotteryPrizeFrontRequest;

import java.util.List;

public interface LotteryPrizeService extends IService<LotteryPrize> {
    List<LotteryPrize> getListByLotteryId(Long id);

    List<LotteryPrize> getFrontListByLotteryId(LotteryPrizeFrontRequest request);
}
