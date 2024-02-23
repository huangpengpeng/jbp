package com.jbp.service.product.comm;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jbp.common.exception.CrmebException;
import com.jbp.common.model.agent.ProductComm;
import com.jbp.common.model.agent.ProductCommConfig;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.user.User;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.service.OrderDetailService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 社群佣金
 */
@Component
public class AssociationCommHandler extends AbstractProductCommHandler {

    @Resource
    private ProductCommService productCommService;
    @Resource
    private OrderDetailService orderDetailService;
    @Resource
    private UserService userService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private UserInvitationService invitationService;
    @Resource
    private ProductCommConfigService productCommConfigService;
    @Resource
    private FundClearingService fundClearingService;

    public Integer getType() {
        return ProductCommEnum.社群佣金.getType();
    }

    @Override
    public Integer order() {
        return 1;
    }

    @Override
    public Boolean saveOrUpdate(ProductComm productComm) {
        // 检查是否存在存在直接更新
        if (productComm.hasError2()) {
            throw new CrmebException(ProductCommEnum.社群佣金.getName() + "参数不完整");
        }
        // 获取规则【解析错误，或者 必要字段不存在 直接在获取的时候抛异常】
        getRule(productComm);
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
        // 对碰的奖励比例是系统级别的配置
        try {
            ProductCommConfig productCommConfig = productCommConfigService.getByType(getType());
            List<Rule> rules = JSONArray.parseArray(productCommConfig.getRatioJson(), Rule.class);
            rules = rules.stream().sorted(Comparator.comparing(Rule::getRatio)).collect(Collectors.toList());
            return rules;
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
        // 增加对碰积分业绩
        List<OrderDetail> orderDetails = orderDetailService.getByOrderNo(order.getOrderNo());
        // 订单总PV
        BigDecimal score = BigDecimal.ZERO;
        for (OrderDetail orderDetail : orderDetails) {
            Integer productId = orderDetail.getProductId();
            ProductComm productComm = productCommService.getByProduct(productId, getType());
            // 佣金不存在或者关闭直接忽略
            if (productComm == null || BooleanUtils.isNotTrue(productComm.getStatus())) {
                continue;
            }
            BigDecimal payPrice = orderDetail.getPayPrice().subtract(orderDetail.getFreightFee()); // 商品总价
            // 总PV
            BigDecimal totalPv = payPrice.add(getWalletDeductionListPv(orderDetail));// 钱包抵扣PV
            totalPv = BigDecimal.valueOf(totalPv.multiply(productComm.getScale()).intValue());
            // 订单总PV
            score = score.add(totalPv);
        }
        // 下单用户信息
        User orderUser = userService.getById(order.getUid());

        List<Rule> rules = getRule(null);
        Map<Integer, Rule> ruleMap = FunctionUtil.keyValueMap(rules, Rule::getCapaXsId);
        Rule maxRule = rules.get(rules.size() - 1);
        BigDecimal maxRatio = maxRule.getRatio(); // 最大比例
        BigDecimal usedRatio = BigDecimal.ZERO; // 已分比例
        Integer uid = order.getUid();
        do {
            Integer pid = invitationService.getPid(uid);
            if (pid == null) {
                break;
            }
            if (ArithmeticUtils.gte(usedRatio, maxRatio)) {
                break;
            }
            UserCapaXs userCapaXs = userCapaXsService.getByUser(pid);
            if (userCapaXs != null) {
                Rule rule = ruleMap.get(userCapaXs.getCapaId());
                if (rule != null) {
                    BigDecimal ratio = rule.getRatio(); // 自己比例
                    BigDecimal usableRatio = ratio.subtract(usedRatio); // 可分比例
                    if (ArithmeticUtils.gt(usableRatio, BigDecimal.ZERO)) {
                        BigDecimal amt = score.multiply(usableRatio).setScale(2, BigDecimal.ROUND_DOWN);
                        if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                            fundClearingService.create(pid, order.getOrderNo(), ProductCommEnum.社群佣金.getName(), amt,
                                    null, null, orderUser.getAccount() + "下单获得" + ProductCommEnum.社群佣金.getName(), "");
                            // 将奖金透传出去
                            int sort = resultList.size() + 1;
                            CommCalculateResult calculateResult = new CommCalculateResult(uid, getType(), ProductCommEnum.社群佣金.getName(),
                                    null, null, score,
                                    1, score, BigDecimal.ONE, ratio, amt, sort);
                            resultList.add(calculateResult);
                            usedRatio = ratio; // 设置已分比例
                        }
                    }
                }
            }
            uid = pid;
        } while (true);


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
        private Integer capaXsId;

        /**
         * 比例
         */
        private BigDecimal ratio;
    }
}
