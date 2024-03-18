package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jbp.common.dto.UserUpperDto;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.FundClearingService;
import com.jbp.service.service.agent.ProductCommConfigService;
import com.jbp.service.service.agent.UserCapaXsService;
import com.jbp.service.service.agent.UserInvitationService;
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
 * 级差伯乐奖
 */
@Component
public class CapaXsBoleCommHandler extends AbstractProductCommHandler {


    @Resource
    private UserInvitationService invitationService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private ProductCommConfigService productCommConfigService;

    @Override
    public Integer getType() {
        return ProductCommEnum.级差伯乐佣金.getType();
    }

    /**
     * 执行顺序在级差将之后
     *
     * @return
     */
    @Override
    public Integer order() {
        return 15;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        return true;
    }

    @Override
    public Rule getRule(ProductComm productComm) {
        try {
            ProductCommConfig config = productCommConfigService.getByType(getType());
            Rule rules = JSONObject.parseObject(config.getRatioJson(), Rule.class);
            return rules;
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
        // 星级级差佣金
        List<CommCalculateResult> collisionFeeList = resultList.stream().filter(r -> r.getType().equals(ProductCommEnum.星级级差佣金.getType()))
                .sorted(Comparator.comparing(CommCalculateResult::getSort)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collisionFeeList)) {
            return;
        }
        // 下单用户的所有上级
        List<UserUpperDto> allUpper = invitationService.getAllUpper(order.getUid());
        if (CollectionUtils.isEmpty(allUpper)) {
            return;
        }
        Rule rule = getRule(null);
        List<LevelRatio> levelRatios = rule.getLevelRatios();
        Map<Integer, LevelRatio> ratioMap = FunctionUtil.keyValueMap(levelRatios, LevelRatio::getLevel);

        int maxLevel = levelRatios.size();
        Map<Integer, UserCapaXs> uidCapaXsMap = userCapaXsService.getUidMap(allUpper.stream().filter(u -> u.getPId() != null).map(UserUpperDto::getPId).collect(Collectors.toList()));
        // 往上找18个人获得佣金
        int i = 1;
        for (CommCalculateResult calculateResult : collisionFeeList) {
            // 得奖人
            Integer uid = calculateResult.getUid();
            User user = userService.getById(calculateResult.getUid());
            Boolean start = false;
            for (UserUpperDto upperDto : allUpper) {
                // 没有上级就空了
                if (upperDto.getPId() == null) {
                    break;
                }
                if (upperDto.getPId().intValue() == uid.intValue()) {
                    start = true;  // 找到自己的位置
                    continue;
                }
                // 自己后面的
                if (start && i <= maxLevel) {
                    UserCapaXs pCapaXs = uidCapaXsMap.get(upperDto.getPId());
                    if (pCapaXs != null && pCapaXs.getCapaId().intValue() >= rule.getCapaXsId().intValue()) {
                        // 算钱
                        BigDecimal ratio = ratioMap.get(i).getRatio();
                        BigDecimal amt = ratio.multiply(calculateResult.getAmt()).setScale(2, BigDecimal.ROUND_DOWN);
                        if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                            fundClearingService.create(upperDto.getPId(), order.getOrderNo(), ProductCommEnum.级差伯乐佣金.getName(), amt,
                                    null, null, user.getAccount() + "获得星级级差佣金奖励上级" + ProductCommEnum.级差伯乐佣金.getName(), "");
                        }
                        i++;
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Rule rule = new Rule();
        rule.setCapaXsId(7L);
        rule.setCapaXsName("一星");
        List<LevelRatio> levelRatios = Lists.newArrayList();
        for (int i = 1; i < 19; i++) {
            LevelRatio levelRatio = new LevelRatio();
            levelRatio.setLevel(i);
            if(i==1 || i==2){
                levelRatio.setRatio(BigDecimal.valueOf(0.15));
            }
            if(i==3 || i==4){
                levelRatio.setRatio(BigDecimal.valueOf(0.1));
            }
            if(i >=5 && i<=10){
                levelRatio.setRatio(BigDecimal.valueOf(0.05));
            }
            if(i > 10){
                levelRatio.setRatio(BigDecimal.valueOf(0.025));
            }

            levelRatios.add(levelRatio);

        }
        rule.setLevelRatios(levelRatios);

        System.out.println(JSONObject.toJSONString(rule));

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
        private Long capaXsId;

        /**
         * 等级
         */
        private String capaXsName;

        /**
         * 比例
         */
        private List<LevelRatio> levelRatios;

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelRatio {
        /**
         * 层级
         */
        private Integer level;

        /**
         * 等级
         */
        private BigDecimal ratio;

    }


}
