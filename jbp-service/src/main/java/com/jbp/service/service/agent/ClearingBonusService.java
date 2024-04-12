package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.ClearingBonus;

import java.util.List;

public interface ClearingBonusService extends IService<ClearingBonus> {

    void insertBatchList(List<ClearingBonus> list);




}
