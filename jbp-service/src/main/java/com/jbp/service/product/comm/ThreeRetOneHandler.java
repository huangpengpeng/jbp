package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.FundClearing;
import com.jbp.common.model.agent.FundClearingProduct;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.FundClearingService;
import com.jbp.service.service.agent.ProductCommConfigService;
import com.jbp.service.service.agent.ProductCommService;
import com.jbp.service.service.agent.UserInvitationService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 推三返一
 */
@Component
public class ThreeRetOneHandler extends AbstractProductCommHandler {

    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserService userService;
    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private UserInvitationService invitationService;


    @Override
    public Integer getType() {
        return ProductCommEnum.推三返一.getType();
    }

    @Override
    public Integer order() {
        return 1;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError()) {
            throw new CrmebException(ProductCommEnum.推三返一.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        List<Rule> rules = getRule(productComm);
        if (CollectionUtils.isEmpty(rules)) {
            throw new CrmebException(ProductCommEnum.推三返一.getName() + "参数不完整");
        }
        for (Rule rule : rules) {
            if (rule.getLevel() == null || StringUtils.isEmpty(rule.getType()) || rule.getValue() == null || ArithmeticUtils.lessEquals(rule.getValue(), BigDecimal.ZERO)) {
                throw new CrmebException(ProductCommEnum.推三返一.getName() + "参数不完整");
            }
        }
        Set<Integer> set = rules.stream().map(Rule::getLevel).collect(Collectors.toSet());
        if (set.size() != rules.size()) {
            throw new CrmebException("单数不能重复");
        }
        // 删除数据库的信息
        productCommService.remove(new LambdaQueryWrapper<ProductComm>()
                .eq(ProductComm::getProductId, productComm.getProductId())
                .eq(ProductComm::getType, productComm.getType()));
        // 保存最新的信息
        productCommService.save(productComm);
        return true;
    }

    @Override
    public List<Rule> getRule(ProductComm productComm) {
        try {
            List<Rule> rules = JSONArray.parseArray(productComm.getRule(), Rule.class);
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
        Integer pid = invitationService.getPid(order.getUid());
        if (pid == null) {
            return;
        }
        BigDecimal totalAmt = BigDecimal.ZERO;
        // 获取订单产品
        List<FundClearingProduct> productList = Lists.newArrayList();
        List<OrderDetail> orderDetails = orderDetailService.getByOrderNo(order.getOrderNo());
        // 历史推三返一单量
        List<FundClearing> fundClearingList = fundClearingService.getByUser(pid, ProductCommEnum.推三返一.getName(), FundClearing.interceptStatus());
        // 根据产品算钱
        for (OrderDetail orderDetail : orderDetails) {
            Integer productId = orderDetail.getProductId();
            // 佣金不存在或者关闭直接忽略
            ProductComm productComm = productCommService.getByProduct(productId, getType());
            if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                continue;
            }
            // 钱包抵扣PV
            BigDecimal totalPv = orderDetailService.getRealScore(orderDetail);
            totalPv = totalPv.multiply(productComm.getScale());
            // 获取佣金规则
            List<Rule> rules = getRule(productComm);
            int i = 1;
            if (CollectionUtils.isNotEmpty(fundClearingList)) {
                i = fundClearingList.size() % rules.size();
                i = i == 0 ? rules.size() : i;
            }
            Map<Integer, Rule> ruleMap = FunctionUtil.keyValueMap(rules, Rule::getLevel);
            Rule rule = ruleMap.get(i);
            BigDecimal amt = BigDecimal.ZERO;
            if (rule != null) {
                if (rule.getType().equals("金额")) {
                    amt = rule.getValue().multiply(BigDecimal.valueOf(orderDetail.getPayNum()));
                } else {
                    amt = rule.getValue().multiply(totalPv);
                }
            }
            // 计算订单金额
            totalAmt = totalAmt.add(amt);
            FundClearingProduct clearingProduct = new FundClearingProduct(productId, orderDetail.getProductName(), totalPv,
                    orderDetail.getPayNum(), BigDecimal.valueOf(i), amt);
            productList.add(clearingProduct);
        }
        // 按订单保存佣金
        totalAmt = totalAmt.setScale(2, BigDecimal.ROUND_DOWN);
        if (ArithmeticUtils.gt(totalAmt, BigDecimal.ZERO)) {
            User orderUser = userService.getById(order.getUid());
            fundClearingService.create(pid, order.getOrderNo(), ProductCommEnum.推三返一.getName(), totalAmt,
                    null, productList, orderUser.getAccount() + "下单获得" + ProductCommEnum.推三返一.getName(), "");
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 单数
         */
        private Integer level;

        /**
         * 比例  金额
         */
        private String type;

        /**
         * 比例
         */
        private BigDecimal value;
    }

}
