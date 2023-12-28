package com.jbp.common.condition;

import com.jbp.common.model.agent.CapaRiseCondition;

public interface ConditionHandler {

    Integer getType();

    String getName();

    void save(CapaRiseCondition riseCondition);
}
