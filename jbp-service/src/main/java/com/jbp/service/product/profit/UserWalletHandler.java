package com.jbp.service.product.profit;


import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alipay.service.schema.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.model.agent.WalletFlow;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderProductProfit;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.common.response.RefundOrderInfoResponse;
import com.jbp.common.utils.ArithmeticUtils;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.service.OrderProductProfitService;
import com.jbp.service.service.RefundOrderService;
import com.jbp.service.service.agent.OrdersRefundMsgService;
import com.jbp.service.service.agent.PlatformWalletService;
import com.jbp.service.service.agent.ProductProfitConfigService;
import com.jbp.service.service.agent.ProductProfitService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 赠送降级积分
 */
@Component
public class UserWalletHandler implements ProductProfitHandler {

    @Resource
    private ProductProfitService productProfitService;
    @Resource
    private PlatformWalletService platformWalletService;
    @Resource
    private OrderProductProfitService orderProductProfitService;
    @Resource
    private ProductProfitConfigService configService;
    @Resource
    private RefundOrderService refundOrderService;
    @Resource
    private OrdersRefundMsgService refundMsgService;

    @Override
    public Integer getType() {
        return ProductProfitEnum.积分.getType();
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
                throw new RuntimeException(ProductProfitEnum.积分.getName() + ":商品权益格式错误0");
            }
            for (Rule rule : rules) {
                if (StringUtil.isEmpty(rule.getWalletName()) || StringUtil.isEmpty(rule.getType())
                        || rule.getWalletType() == null || rule.getValue() == null) {
                    throw new RuntimeException(ProductProfitEnum.积分.getName() + ":商品权益格式错误00");
                }
                if (ArithmeticUtils.lessEquals(rule.getValue(), BigDecimal.ZERO)) {
                    throw new RuntimeException(ProductProfitEnum.积分.getName() + ":奖励数值必须大于0");
                }
            }
            return rules;
        } catch (Exception e) {
            throw new RuntimeException(ProductProfitEnum.积分.getName() + ":商品权益格式错误1");
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

        for (ProductProfit productProfit : productProfitList) {
            List<Rule> ruleList = getRule(productProfit.getRule());
            // 商品
            OrderDetail orderDetail = orderDetailMap.get(productProfit.getProductId());
            // 商品支付金额
            BigDecimal payPrice = orderDetail.getPayPrice().multiply(BigDecimal.valueOf(orderDetail.getPayNum()));
            // 奖励金额
            StringBuilder profitPostscript  = new StringBuilder();
            BigDecimal amt = BigDecimal.ZERO;
            for (Rule rule : ruleList) {
                if ("金额".equals(rule.getType())) {
                    amt = rule.getValue().multiply(BigDecimal.valueOf(orderDetail.getPayNum()));
                }
                if ("比例".equals(rule.getType())) {
                    amt = payPrice.multiply(rule.getValue()).setScale(2, BigDecimal.ROUND_DOWN);
                }
                String postscript = StrUtil.format("订单支付奖励，单号:{}, 金额:{}元", order.getOrderNo(), amt);
                if (ArithmeticUtils.gt(amt, BigDecimal.ZERO)) {
                    platformWalletService.transferToUser(order.getUid(), rule.walletType, amt,
                            WalletFlow.OperateEnum.奖励.toString(), order.getOrderNo(), postscript);
                    profitPostscript.append(rule.getWalletName() + ":" + amt).append("元").append("/");
                }
            }
            // 订单权益记录
            orderProductProfitService.save(order.getId(), order.getOrderNo(), productProfit.getProductId(), getType(),
                    ProductProfitEnum.积分.getName(), JSONArray.toJSONString(ruleList), profitPostscript.toString());
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
            String context = "购买订单增加【"+ProductProfitEnum.积分.getName()+"】, 商品名称【" + refundDetail.getProductName() + "】收益名称【" + productProfit.getProfitName() + "】 收益说明【" + productProfit.getPostscript() + "】";
            refundMsgService.create(orderNo, refundOrder.getRefundOrderNo(), context);
        }
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
    }
}
