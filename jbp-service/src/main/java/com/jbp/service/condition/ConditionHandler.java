package com.jbp.service.condition;

import com.jbp.common.model.agent.CapaRiseCondition;

public interface ConditionHandler {

    Integer getType();

    String getName();

    void save(CapaRiseCondition riseCondition);


    <T>T getRule(CapaRiseCondition riseCondition);
}
