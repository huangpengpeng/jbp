package com.jbp.service.condition;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.model.agent.RiseCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 星级_小区业绩独立线人数
 */
@Component
public class XsIsolateLineHandler implements ConditionHandler {

    @Override
    public String getName() {
        return ConditionEnum.星级_小区业绩独立线人数.getName();
    }

    @Override
    public void valid(RiseCondition riseCondition){
        getRule(riseCondition);
    }

    @Override
    public XsIsolateLineHandler.Rule getRule(RiseCondition riseCondition) {
        try {
            Rule rule = JSONObject.parseObject(riseCondition.getValue()).toJavaObject(Rule.class);
            if (rule.getIndeCount() == null || rule.getIndeCapaId() == null
                    || rule.getTeamAmt() == null) {
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
         * 独立线条数
         */
        private Integer indeCount;

        /**
         * 独立线条数等级
         */
        private Long indeCapaId;

        /**
         * 独立线条数等级名称
         */
        private String indeCapaName;

        /**
         * 小区业绩
         */
        private BigDecimal teamAmt;
    }
}
