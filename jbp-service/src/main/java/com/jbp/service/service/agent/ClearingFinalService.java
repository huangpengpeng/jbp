package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.ClearingFinal;

public interface ClearingFinalService extends IService<ClearingFinal> {

    ClearingFinal create(String commName, Integer commType, String startTime, String endTime);

    ClearingFinal getByName(String name);

    /**
     * 获取上一次结算
     */
    ClearingFinal getLastOne(Long id, Integer commType);
}
