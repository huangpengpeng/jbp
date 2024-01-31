package com.jbp.service.service.agent;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.agent.RiseCondition;

public interface CapaRiseConditionService extends IService<RiseCondition> {

    RiseCondition add(String name, String description, String value);
}
