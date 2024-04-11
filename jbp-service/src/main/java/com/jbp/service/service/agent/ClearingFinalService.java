package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.ClearingFinal;

public interface ClearingFinalService extends IService<ClearingFinal> {

    ClearingFinal create(String commName, String startTime, String endTime);

    ClearingFinal getByName(String name);
}
