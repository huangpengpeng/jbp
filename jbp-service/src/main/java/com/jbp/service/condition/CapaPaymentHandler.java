package com.jbp.service.condition;

import com.jbp.common.model.agent.RiseCondition;

/**
 * 单笔支付升级
 */
public class CapaPaymentHandler implements ConditionHandler {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public void valid(RiseCondition riseCondition) {

    }

    @Override
    public <T> T getRule(RiseCondition riseCondition) {
        return null;
    }

    @Override
    public Boolean isOk(Integer uid, RiseCondition riseCondition) {
        return null;
    }
}
