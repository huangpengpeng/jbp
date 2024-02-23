package com.jbp.service.condition;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.common.model.order.Order;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.agent.UserCapaService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 单笔支付升级
 */
@Component
public class CapaPaymentHandler implements ConditionHandler {

    @Resource
    private OrderService orderService;
    @Resource
    private UserCapaService userCapaService;


    @Override
    public String getName() {
        return ConditionEnum.单笔金额升级.getName();
    }

    @Override
    public void valid(RiseCondition riseCondition) {
        getRule(riseCondition);
    }


    @Override
    public Rule getRule(RiseCondition riseCondition) {
        try {
            Rule rule = JSONObject.parseObject(riseCondition.getValue()).toJavaObject(Rule.class);
            if (rule.getAmt() == null || ArithmeticUtils.lessEquals(rule.getAmt(), BigDecimal.ZERO)) {
                throw new RuntimeException(getName() + ":升级规则格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(getName() + ":升级规则格式错误" + e.getMessage());
        }
    }

    @Override
    public Boolean isOk(Integer uid, RiseCondition riseCondition) {
        // 找最近的一笔平台订单
        Order order = orderService.getOne(new QueryWrapper<Order>().lambda()
                .eq(Order::getUid, uid).eq(Order::getPaid, true)
                .eq(Order::getLevel, 0)
                .orderByDesc(Order::getId)
                .last(" limit 1"));
        if (order == null) {
            return false;
        }
        Rule rule = getRule(riseCondition);
        return ArithmeticUtils.gte(order.getProTotalPrice(), rule.getAmt());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /**
         * 订货金额
         */
        private BigDecimal amt;
    }
}
