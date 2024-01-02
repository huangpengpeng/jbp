package com.jbp.service.condition;

import com.jbp.common.model.agent.CapaRiseCondition;
import com.jbp.service.service.CapaRiseConditionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 邀请一阶条件
 */
@Component
public class InviteOneLevelHandler implements ConditionHandler {

    @Resource
    private CapaRiseConditionService capaRiseConditionService;

    @Override
    public Integer getType() {
        return ConditionEnum.等级_直属一阶等级人数.getType();
    }

    @Override
    public String getName() {
        return ConditionEnum.等级_直属一阶等级人数.getName();
    }

    @Override
    public void save(CapaRiseCondition riseCondition) {
        getRule(riseCondition);
        capaRiseConditionService.save(riseCondition);
    }

    @Override
    public InviteOneLevelHandler.Rule getRule(CapaRiseCondition riseCondition) {
        try {
            Rule rule = riseCondition.getValue().toJavaObject(Rule.class);
            if (rule.getNum() == null || rule.getCapaId() == null) {
                throw new RuntimeException(getName() + ":升级规则格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(getName() + ":升级规则格式错误" + e.getMessage());
        }
    }

    @Override
    public Boolean isOk(Integer uid, CapaRiseCondition riseCondition) {
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
