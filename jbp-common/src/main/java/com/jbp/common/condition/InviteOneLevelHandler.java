package com.jbp.common.condition;

import com.jbp.common.model.agent.CapaRiseCondition;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 邀请一阶条件
 */
@Component
public class InviteOneLevelHandler implements ConditionHandler{

    @Override
    public Integer getType() {
        return 0;
    }

    @Override
    public String getName() {
        return "等级_邀请一阶条件";
    }

    @Override
    public void save(CapaRiseCondition riseCondition) {

    }

    @Data
    class Rule{

        /**
         * 一阶人数
         */
        private int num;

        /**
         * 等级要求
         */
        private Long capaId;

    }
}
