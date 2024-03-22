package com.jbp.service.condition;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaSnapshot;
import com.jbp.common.model.order.Order;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.service.OrderService;
import com.jbp.service.service.agent.UserCapaService;
import com.jbp.service.service.agent.UserCapaSnapshotService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 补差升级条件
 */
@Component
public class CapaRepairDifferenceHandler implements ConditionHandler {

    @Resource
    private OrderService orderService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private UserCapaSnapshotService snapshotService;

    @Override
    public String getName() {
        return ConditionEnum.补差金额升级.getName();
    }

    @Override
    public void valid(RiseCondition riseCondition) {
        getRule(riseCondition);
    }

    @Override
    public List<Rule> getRule(RiseCondition riseCondition) {
        try {
            List<Rule> list = JSONArray.parseArray(riseCondition.getValue(), Rule.class);
            if (CollectionUtils.isEmpty(list)) {
                throw new RuntimeException(getName() + ":升级规则不能为空");
            }
            for (Rule rule : list) {
                if (rule.getRepairDifference() == null || ArithmeticUtils.lessEquals(rule.getRepairDifference(), BigDecimal.ZERO)) {
                    throw new RuntimeException(getName() + ":升级规则格式错误0");
                }
            }
            return list;
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
                .eq(Order::getPlatform, "报单")
                .orderByDesc(Order::getId)
                .last(" limit 1"));

        if (order == null) {
            return false;
        }

        UserCapa userCapa = userCapaService.getByUser(uid);
        if (userCapa == null) {
            return false;
        }
        List<UserCapaSnapshot> snapshots = snapshotService.getByDescription(order.getOrderNo());
        if (!snapshots.isEmpty()) {
            return false;
        }
        List<Rule> list = getRule(riseCondition);
        Map<Long, Rule> ruleMap = FunctionUtil.keyValueMap(list, Rule::getOrgCapaId);
        Rule rule = ruleMap.get(userCapa.getCapaId());
        if (rule == null) {
            return false;
        }
        return ArithmeticUtils.gte(order.getProTotalPrice(), rule.getRepairDifference());
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 原等级
         */
        private Long orgCapaId;

        /**
         * 补差金额
         */
        private BigDecimal repairDifference;
    }
}
