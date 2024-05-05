package com.jbp.service.product.profit;


import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alipay.service.schema.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.model.agent.WalletConfig;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderProductProfit;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.common.model.user.User;
import com.jbp.common.response.RefundOrderInfoResponse;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.service.OrderProductProfitService;
import com.jbp.service.service.RefundOrderService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.WalletConfigService;
import com.jbp.service.service.agent.OrdersRefundMsgService;
import com.jbp.service.service.agent.ProductProfitConfigService;
import com.jbp.service.service.agent.ProductProfitService;
import com.jbp.service.service.agent.WalletGivePlanService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 赠送降级积分
 */
@Component
public class UserWalletMonthHandler implements ProductProfitHandler {

    @Resource
    private UserService userService;
    @Resource
    private ProductProfitService productProfitService;
    @Resource
    private OrderProductProfitService orderProductProfitService;
    @Resource
    private ProductProfitConfigService configService;
    @Resource
    private RefundOrderService refundOrderService;
    @Resource
    private OrdersRefundMsgService refundMsgService;
    @Resource
    private WalletGivePlanService walletGivePlanService;
    @Resource
    private WalletConfigService walletConfigService;

    @Override
    public Integer getType() {
        return ProductProfitEnum.积分按月赠送.getType();
    }

    @Override
    public void save(ProductProfit productProfit) {
        getRule(productProfit.getRule());
        productProfitService.remove(new QueryWrapper<ProductProfit>().lambda().eq(ProductProfit::getProductId,
                productProfit.getProductId()).eq(ProductProfit::getType, productProfit.getType()));
        productProfitService.save(productProfit);
    }

    @Override
    public List<Rule> getRule(String ruleStr) {
        try {
            List<Rule> rules = JSONArray.parseArray(ruleStr, Rule.class);
            if (CollectionUtils.isEmpty(rules)) {
                throw new RuntimeException(ProductProfitEnum.积分按月赠送.getName() + ":商品权益格式错误0");
            }
            for (Rule rule : rules) {
                if (StringUtil.isEmpty(rule.getWalletName()) || StringUtil.isEmpty(rule.getType())
                        || rule.getWalletType() == null || rule.getValue() == null) {
                    throw new RuntimeException(ProductProfitEnum.积分按月赠送.getName() + ":商品权益格式错误00");
                }
                if (ArithmeticUtils.lessEquals(rule.getValue(), BigDecimal.ZERO)) {
                    throw new RuntimeException(ProductProfitEnum.积分按月赠送.getName() + ":奖励数值必须大于0");
                }
                if (rule.getMonthNum() == null) {
                    throw new RuntimeException(ProductProfitEnum.积分按月赠送.getName() + ":连续赠送月份不能为空");
                }
            }
            return rules;
        } catch (Exception e) {
            throw new RuntimeException(ProductProfitEnum.积分按月赠送.getName() + ":商品权益格式错误1");
        }
    }

    @Override
    public void orderSuccess(Order order, List<OrderDetail> orderDetailList, List<ProductProfit> productProfitList) {
        Map<Integer, OrderDetail> orderDetailMap = FunctionUtil.keyValueMap(orderDetailList, OrderDetail::getProductId);
        ProductProfitConfig profitConfig = configService.getByType(getType());
        if (profitConfig == null || !BooleanUtil.isTrue(profitConfig.getIfOpen())) {
            return;
        }
        productProfitList = ListUtils.emptyIfNull(productProfitList).stream().filter(p -> p.getType() == getType()
                && BooleanUtil.isTrue(p.getStatus())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(productProfitList)) {
            return;
        }

        User user = userService.getById(order.getUid());

        for (ProductProfit productProfit : productProfitList) {
            List<Rule> ruleList = getRule(productProfit.getRule());
            // 商品
            OrderDetail orderDetail = orderDetailMap.get(productProfit.getProductId());
            // 商品支付金额
            BigDecimal payPrice = orderDetail.getPayPrice().multiply(BigDecimal.valueOf(orderDetail.getPayNum()));
            // 奖励金额
            Date now = DateTimeUtils.getNow();
            StringBuilder profitPostscript = new StringBuilder();
            BigDecimal amt = BigDecimal.ZERO;
            for (Rule rule : ruleList) {
                if ("金额".equals(rule.getType())) {
                    amt = rule.getValue().multiply(BigDecimal.valueOf(orderDetail.getPayNum()));
                }
                if ("比例".equals(rule.getType())) {
                    amt = payPrice.multiply(rule.getValue()).setScale(2, BigDecimal.ROUND_DOWN);
                }
                String postscript = "赠送"+rule.getWalletName() + "积分:" + amt + "连续赠送:" + rule.getMonthNum() + "月";
                if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                    profitPostscript.append(postscript).append("/");
                }
                WalletConfig walletConfig = walletConfigService.getByType(rule.getWalletType());
                for (int i = 0; i < rule.getMonthNum(); i++) {
                    String planTime = DateTimeUtils.format(DateTimeUtils.getMonthStart(DateTimeUtils.addMonths(now, i)), DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN);
                    walletGivePlanService.add(user, walletConfig, amt, order.getOrderNo(), postscript, planTime);
                }
            }
            // 订单权益记录
            orderProductProfitService.save(order.getId(), order.getOrderNo(), productProfit.getProductId(), getType(),
                    ProductProfitEnum.积分按月赠送.getName(), JSONArray.toJSONString(ruleList), profitPostscript.toString());
        }
    }

    @Override
    public void orderRefund(Order order, RefundOrder refundOrder) {
        // 平台单号
        String orderNo = StringUtil.isEmpty(order.getPlatOrderNo()) ? order.getOrderNo() : order.getPlatOrderNo();
        List<OrderProductProfit> list = orderProductProfitService.getByOrder(orderNo, getType(), OrderProductProfit.Constants.成功.name());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        RefundOrderInfoResponse refundDetail = refundOrderService.getRefundOrderDetailByRefundOrderNo(refundOrder.getRefundOrderNo());
        List<OrderProductProfit> productProfits = list.stream().filter(o -> o.getProductId().equals(refundDetail.getProductId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(productProfits)) {
            return;
        }
        for (OrderProductProfit productProfit : productProfits) {
            String context = "购买订单增加【"+ProductProfitEnum.积分按月赠送.getName()+"】, 商品名称【" + refundDetail.getProductName() + "】收益名称【" + productProfit.getProfitName() + "】 收益说明【" + productProfit.getPostscript() + "】";
            refundMsgService.create(orderNo, refundOrder.getRefundOrderNo(), context);
        }
        walletGivePlanService.cancel(orderNo);
    }

    /**
     * 当前权益对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        /**
         * 钱包类型
         */
        private Integer walletType;

        /**
         * 钱包名称
         */
        private String walletName;

        /**
         * 类型  0 比例  1 金额
         */
        private String type ;

        /**
         * 数字
         */
        private BigDecimal value ;

        /**
         * 连续赠送几个月
         */
        private Integer monthNum ;

    }
}
