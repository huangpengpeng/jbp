package com.jbp.service.condition;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.model.agent.*;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.service.agent.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.logstash.logback.encoder.org.apache.commons.lang.BooleanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 关联等级升星
 */
@Component
public class CapaXsJoinCapaHandler implements ConditionHandler {

    @Autowired
    private UserCapaService userCapaService;


    @Override
    public String getName() {
        return ConditionEnum.关联等级升星.getName();
    }

    @Override
    public void valid(RiseCondition riseCondition) {
        getRule(riseCondition);
    }

    @Override
    public Rule getRule(RiseCondition riseCondition) {
        try {
            Rule rule = JSONObject.parseObject(riseCondition.getValue()).toJavaObject(Rule.class);
            if (rule.getCapaId() == null) {
                throw new RuntimeException(getName() + ":升级规则格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(getName() + ":升级规则格式错误" + e.getMessage());
        }
    }

    @Override
    public Boolean isOk(Integer uid, RiseCondition riseCondition) {
        // 用户当前等级大于指定等级
        Rule rule = getRule(riseCondition);
        UserCapa userCapa = userCapaService.getByUser(uid);
        return userCapa != null && NumberUtils.compare(rule.getCapaId(), userCapa.getCapaId()) <= 0;
    }

    /**
     * 升级条件规则信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 最低等级要求
         */
        private Long capaId;
    }
}
