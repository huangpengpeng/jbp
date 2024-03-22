package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.FundClearingService;
import com.jbp.service.service.agent.ProductCommConfigService;
import com.jbp.service.service.agent.ProductCommService;
import com.jbp.service.service.agent.UserCapaService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 深度佣金根据渠道佣金而来
 */
@Component
public class DepthCommHandler extends AbstractProductCommHandler {

    @Resource
    private UserService userService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private ProductCommConfigService productCommConfigService;

    @Override
    public Integer getType() {
        return ProductCommEnum.深度佣金.getType();
    }

    @Override
    public Integer order() {
        return 10;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        return true;
    }

    @Override
    public List<Rule> getRule(ProductComm productComm) {
        // 对碰的奖励比例是系统级别的配置
        try {
            ProductCommConfig productCommConfig = productCommConfigService.getByType(getType());
            return JSONArray.parseArray(productCommConfig.getRatioJson(), Rule.class);
        } catch (Exception e) {
            throw new CrmebException(getType() + ":佣金格式解析失败:" + productComm.getRule());
        }
    }

    @Override
    public void orderSuccessCalculateAmt(Order order, LinkedList<CommCalculateResult> resultList) {
        ProductCommConfig productCommConfig = productCommConfigService.getByType(getType());
        if (!productCommConfig.getIfOpen()) {
            return;
        }
        // 对碰奖金
        List<CommCalculateResult> collisionFeeList = resultList.stream().filter(r -> r.getType().equals(ProductCommEnum.渠道佣金.getType()))
                .sorted(Comparator.comparing(CommCalculateResult::getSort)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collisionFeeList)) {
            return;
        }
        // 得奖比例
        List<Rule> rules = getRule(null);
        Map<Long, Rule> ruleMap = FunctionUtil.keyValueMap(rules, Rule::getCapaId);
        // 总积分
        BigDecimal totalScore = collisionFeeList.get(0).getPrice();
        for (CommCalculateResult calculateResult : collisionFeeList) {
            BigDecimal score = calculateResult.getPv();
            BigDecimal minScore = BigDecimal.valueOf(Math.min(totalScore.doubleValue(), score.doubleValue()));
            if (ArithmeticUtils.gt(minScore, BigDecimal.ZERO)) {
                UserCapa userCapa = userCapaService.getByUser(calculateResult.getUid());
                Rule rule = userCapa == null ? null : ruleMap.get(userCapa.getCapaId());
                BigDecimal ratio = rule == null ? BigDecimal.ZERO : rule.getRatio();
                BigDecimal amt = minScore.multiply(ratio).setScale(2, BigDecimal.ROUND_DOWN);
                if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {

                    User user = userService.getById(calculateResult.getUid());
                    fundClearingService.create(calculateResult.getUid(), order.getOrderNo(), ProductCommEnum.深度佣金.getName(), amt,
                            null, null, user.getAccount() + "获得对碰佣金，奖励" + ProductCommEnum.深度佣金.getName(), "");
                }
                totalScore = totalScore.subtract(minScore);
            } else {
                break;
            }
        }
    }

    public static void main(String[] args) {
        List<Rule> list = Lists.newArrayList();
        for (int i = 1; i <= 5 ; i++) {
            Rule rule = new Rule(Long.valueOf(i), BigDecimal.valueOf(0));
            if(i==4){
                rule.setRatio(BigDecimal.valueOf(0.05));
            }
            if(i==5){
                rule.setRatio(BigDecimal.valueOf(0.1));
            }
            list.add(rule);
        }
        System.out.println(JSONArray.toJSONString(list));

    }


    /**
     * 渠道佣金规则
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /**
         * 等级
         */
        private Long capaId;

        /**
         * 比例
         */
        private BigDecimal ratio;
    }
}
