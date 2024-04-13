package com.jbp.service.condition;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.agent.RiseCondition;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.service.agent.FundClearingService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CapaXsIncomeHandler implements ConditionHandler {

    @Resource
    private FundClearingService fundClearingService;

    @Override
    public String getName() {
        return ConditionEnum.累积收益升星.getName();
    }

    @Override
    public void valid(RiseCondition riseCondition) {
        getRule(riseCondition);
    }

    @Override
    public Rule  getRule(RiseCondition riseCondition) {
        try {
            Rule rule = JSONObject.parseObject(riseCondition.getValue(), Rule.class);
            if (rule.getFee() == null) {
                throw new RuntimeException(getName() + ":升级规则收益总额不能为空");
            }
            if (CollectionUtils.isEmpty(rule.getCommList())) {
                throw new RuntimeException(getName() + ":升级规则佣金类型不能为空");
            }

            return rule;
        } catch (Exception e) {
            throw new RuntimeException(getName() + ":升级规则格式错误" + e.getMessage());
        }
    }

    @Override
    public Boolean isOk(Integer uid, RiseCondition riseCondition) {
        Rule rule = getRule(riseCondition);
        String[] array = rule.getCommList().stream().map(CommList::getCommName).toArray(String[]::new);
        BigDecimal sendCommAmt = fundClearingService.getSendCommAmt(uid, null, null, array);
        if(sendCommAmt != null && ArithmeticUtils.gte(sendCommAmt, rule.getFee())){
            return true;
        }
        return false;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 指定佣金类型
         */
        private List<CommList> commList;

        /**
         * 累积金额
         */
        private BigDecimal fee;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommList {

        /**
         * 佣金类型
         */
        private Integer  type;

        /**
         * 佣金名称
         */
        private String commName;
    }
}
