package com.jbp.service.product.comm;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import com.beust.jcommander.internal.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.FundClearingService;
import com.jbp.service.service.agent.ProductCommConfigService;
import com.jbp.service.service.agent.UserInvitationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
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
    @Autowired
    private Environment environment;


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
    public void orderSuccessCalculateAmt(Order order, List<OrderDetail> orderDetails, LinkedList<CommCalculateResult> resultList) {

        List<CommCalculateResult> collisionFeeList = resultList.stream().filter(r -> r.getType().equals(ProductCommEnum.店铺佣金.getType()))
                .sorted(Comparator.comparing(CommCalculateResult::getSort)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collisionFeeList)) {
            return;
        }
        User orderUser = userService.getById(order.getUid());
        BigDecimal ratio = getRule(null).getRatio();
        for (CommCalculateResult calculateResult : collisionFeeList) {

            Integer uid = calculateResult.getUid();
            String internalUid = environment.getProperty("internal.shop");
            if (StringUtils.isNotEmpty(internalUid) && internalUid.equals("1")) {
                User user = userService.getById(uid);
                Map<String, Object> map = SqlRunner.db().selectOne("select * from user_shop_band where account={0} limit 1", user.getAccount());
                if (map != null) {
                    String paccount = MapUtils.getString(map, "paccount");
                    BigDecimal amt = calculateResult.getPv().multiply(ratio).setScale(2, BigDecimal.ROUND_DOWN);
                    if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                        fundClearingService.create(userService.getByAccount(paccount).getId(), order.getOrderNo(), ProductCommEnum.推荐店铺佣金.getName(), amt,
                                null, orderUser.getAccount() + "下单, 奖励" + ProductCommEnum.推荐店铺佣金.getName(), "");
                    }
                    return;
                }
            }
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
