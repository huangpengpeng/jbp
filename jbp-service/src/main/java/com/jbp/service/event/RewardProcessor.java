package com.jbp.service.event;

import com.jbp.common.dto.RewardContextDTO;
import com.jbp.common.model.agent.LotteryRecord;

public interface RewardProcessor<T> {

	LotteryRecord doReward(RewardContextDTO context);

}
