package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.StringUtils;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 众合运营佣金
 */
@Component
public class ZHCXYunYingCommHandler extends AbstractProductCommHandler {

    @Resource
    private FundClearingService fundClearingService;
    @Resource
    private ProductCommService productCommService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserService userService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private UserInvitationJumpService userInvitationJumpService;
    @Resource
    private FundClearingRecordService fundClearingRecordService;

    @Override
    public Integer getType() {
        return ProductCommEnum.运营佣金.getType();
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {

        // 检查是否存在存在直接更新
        if (productComm.hasError()) {
            throw new CrmebException(ProductCommEnum.运营佣金.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        List<Rule> rules = getRule(productComm);
        if (CollectionUtils.isEmpty(rules)) {
            throw new CrmebException(ProductCommEnum.运营佣金.getName() + "参数不完整");
        }
        for (Rule rule : rules) {
            if (rule.getSort() == null || StringUtils.isEmpty(rule.getType()) || rule.getValue() == null || ArithmeticUtils.lessEquals(rule.getValue(), BigDecimal.ZERO)) {
                throw new CrmebException(ProductCommEnum.运营佣金.getName() + "参数不完整");
            }
        }
        Set<Integer> set = rules.stream().map(Rule::getSort).collect(Collectors.toSet());
        if (set.size() != rules.size()) {
            throw new CrmebException("序号不能重复");
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
    public void orderSuccessCalculateAmt(Order order, List<OrderDetail> orderDetails, LinkedList<CommCalculateResult> resultList) {
        Integer pid = userInvitationJumpService.getOrgPid(order.getUid());
        if (pid == null) {
            return;
        }

        Integer max = 0;
        for (OrderDetail orderDetail : orderDetails) {
            ProductComm productComm = productCommService.getByProduct(orderDetail.getProductId(), getType());
            if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                continue;
            }
            List<Rule> rule = getRule(productComm);
            if (CollectionUtils.isNotEmpty(rule) && rule.size() > max) {
                max = rule.size();
            }
        }
        if (max.intValue() == 0) {
            return;
        }
        // 获取最近的几个店铺  查询的店铺数量根据商品设置的最大分佣人数确认
        LinkedList<Integer> pidList = new LinkedList<>();
        do {
            if (pid == null) {
                break;
            }
            if (pidList.size() >= max.intValue()) {
                break;
            }
            Boolean openShop = userService.getById(pid).getOpenShop();
            if (openShop != null && openShop) {
                pidList.add(pid);
            }
            pid = userInvitationJumpService.getOrgPid(pid);
        } while (true);

        if (pidList.isEmpty()) {
            return;
        }

        User orderUser = userService.getById(order.getUid());
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
            for (int i = 1; i <= rules.size(); i++) {
                Rule rule = rules.get(i - 1);
                if (pidList.size() >= i) {
                    BigDecimal amt = BigDecimal.ZERO;

                    if (rule.getType().equals("金额")) {
                        amt = rule.getValue().multiply(BigDecimal.valueOf(orderDetail.getPayNum()));
                    } else {
                        amt = rule.getValue().multiply(totalPv);
                    }
                    User user = userService.getById(pidList.get(i - 1));
                    fundClearingRecordService.create(user, orderUser, orderDetail, ProductCommEnum.运营佣金.getName(), amt, totalPv, rule.getValue(), rule.getType(), "获得层级:" + i);
                }
            }
        }
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 序号
         */
        private Integer sort;

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
