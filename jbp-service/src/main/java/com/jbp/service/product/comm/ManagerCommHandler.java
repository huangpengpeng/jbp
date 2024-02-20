package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONObject;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
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
import java.util.stream.Collectors;

/**
 * 管理佣金
 */
@Component
public class ManagerCommHandler extends AbstractProductCommHandler {

    @Resource
    private UserService userService;
    @Resource
    private UserInvitationService userInvitationService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private ProductCommConfigService productCommConfigService;

    @Override
    public Integer getType() {
        return ProductCommEnum.管理佣金.getType();
    }

    @Override
    public Integer order() {
        return 10;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        return null;
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
        if (!productCommConfig.getStatus()) {
            return;
        }
        // 社群佣金
        List<CommCalculateResult> collisionFeeList = resultList.stream().filter(r -> r.getType().equals(ProductCommEnum.社群佣金.getType()))
                .sorted(Comparator.comparing(CommCalculateResult::getSort)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collisionFeeList)) {
            return;
        }
        // 得奖比例
        Rule rule = getRule(null);
        for (CommCalculateResult calculateResult : collisionFeeList) {
            User user = userService.getById(calculateResult.getUid());
            Integer uid = calculateResult.getUid();
            int i = 0;
            do {
                Integer pid = userInvitationService.getPid(uid);
                if (pid == null) {
                    break;
                }
                UserCapaXs userCapaXs = userCapaXsService.getByUser(pid);
                if (userCapaXs != null) {
                    BigDecimal amt = calculateResult.getAmt().multiply(rule.getRatio()).setScale(2, BigDecimal.ROUND_DOWN);
                    if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                        fundClearingService.create(pid, order.getOrderNo(), ProductCommEnum.管理佣金.getName(), amt,
                                null, null, user.getAccount() + "获得社群佣金, 奖励" + ProductCommEnum.管理佣金.getName(), "");
                    }
                    i++;
                }
                uid = pid;
            } while (i < rule.getLevel());
        }

    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /**
         * 层级
         */
        private Integer level;

        /**
         * 比例
         */
        private BigDecimal ratio;
    }
}
