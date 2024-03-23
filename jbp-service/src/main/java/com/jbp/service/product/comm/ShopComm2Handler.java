package com.jbp.service.product.comm;


import com.alibaba.fastjson.JSONObject;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.FundClearingService;
import com.jbp.service.service.agent.ProductCommConfigService;
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
import java.util.stream.Collectors;

/**
 * 推荐管理佣金
 */
@Component
public class ShopComm2Handler extends AbstractProductCommHandler {

    @Resource
    private UserService userService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private FundClearingService fundClearingService;


    @Override
    public Integer getType() {
        return ProductCommEnum.推荐店铺佣金.getType();
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
    public Rule getRule(ProductComm productComm) {
        try {
            ProductCommConfig productCommConfig = productCommConfigService.getByType(getType());
            return JSONObject.parseObject(productCommConfig.getRatioJson(), Rule.class);
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
        List<CommCalculateResult> collisionFeeList = resultList.stream().filter(r -> r.getType().equals(ProductCommEnum.店铺佣金.getType()))
                .sorted(Comparator.comparing(CommCalculateResult::getSort)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collisionFeeList)) {
            return;
        }
        User orderUser = userService.getById(order.getUid());
        BigDecimal ratio = getRule(null).getRatio();
        for (CommCalculateResult calculateResult : collisionFeeList) {

            Integer uid = calculateResult.getUid();
            Integer pid = invitationService.getPid(uid);
            BigDecimal amt = calculateResult.getPv().multiply(ratio).setScale(2, BigDecimal.ROUND_DOWN);
            if (pid != null && ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                fundClearingService.create(pid, order.getOrderNo(), ProductCommEnum.推荐店铺佣金.getName(), amt,
                         null, orderUser.getAccount() + "下单, 奖励" + ProductCommEnum.推荐店铺佣金.getName(), "");
            }
        }
    }

    public static void main(String[] args) {
        Rule rule = new Rule();
        rule.setRatio(BigDecimal.valueOf(0.05));
        System.out.println(JSONObject.toJSONString(rule));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 比例
         */
        private BigDecimal ratio;
    }
}
