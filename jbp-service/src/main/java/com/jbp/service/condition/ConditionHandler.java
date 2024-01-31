package com.jbp.service.condition;

import com.jbp.common.model.agent.RiseCondition;

import java.util.List;

public interface ConditionHandler {

    String getName();

    void valid(RiseCondition riseCondition);

    <T>T getRule(RiseCondition riseCondition);

    Boolean  isOk(Integer uid, RiseCondition riseCondition);
}
