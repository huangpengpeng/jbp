package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.ClearingBonus;
import com.jbp.common.model.agent.ClearingBonusFlow;

import java.util.List;

public interface ClearingBonusFlowService extends IService<ClearingBonusFlow> {

    void insertBatchList(List<ClearingBonusFlow> list);
}
