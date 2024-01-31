package com.jbp.service.condition;

import com.jbp.common.model.agent.RiseCondition;

public interface ConditionHandler {

    String getName();

    void save(RiseCondition riseCondition);

    <T>T getRule(RiseCondition riseCondition);

    Boolean  isOk(Integer uid, RiseCondition riseCondition);
}
