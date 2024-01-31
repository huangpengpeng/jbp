package com.jbp.service.condition;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.model.agent.RiseCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 邀请一阶条件
 */
@Component
public class InviteOneLevelHandler implements ConditionHandler {



    @Override
    public String getName() {
        return ConditionEnum.等级_直属一阶等级人数.getName();
    }

    @Override
    public void valid(RiseCondition riseCondition){
        getRule(riseCondition);
    }

    @Override
    public InviteOneLevelHandler.Rule getRule(RiseCondition riseCondition) {
        try {
            Rule rule = JSONObject.parseObject(riseCondition.getValue()).toJavaObject(Rule.class);
            if (rule.getNum() == null || rule.getCapaId() == null) {
                throw new RuntimeException(getName() + ":升级规则格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(getName() + ":升级规则格式错误" + e.getMessage());
        }
    }

    @Override
    public Boolean isOk(Integer uid, RiseCondition riseCondition) {
        // 当前用户是否满足改升级条件  满足返回 true  不满足返回false
        return null;
    }

    /**
     * 升级条件规则信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 一阶人数
         */
        private Integer num;

        /**
         * 等级要求
         */
        private Long capaId;

    }
}
