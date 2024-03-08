package com.jbp.service.product.profit;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alipay.service.schema.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderProductProfit;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.common.model.user.WhiteUser;
import com.jbp.common.response.RefundOrderInfoResponse;
import com.jbp.service.service.OrderProductProfitService;
import com.jbp.service.service.RefundOrderService;
import com.jbp.service.service.WhiteUserService;
import com.jbp.service.service.agent.OrdersRefundMsgService;
import com.jbp.service.service.agent.ProductProfitConfigService;
import com.jbp.service.service.agent.ProductProfitService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 赠送白名单处理器
 */
@Component
public class UserWhiteHandler implements ProductProfitHandler {

    @Resource
    private ProductProfitService productProfitService;
    @Resource
    private WhiteUserService  whiteUserService;
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
        return ProductProfitEnum.白名单.getType();
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
                throw new RuntimeException(ProductProfitEnum.白名单.getName() + ":商品权益格式错误0");
            }
            for (Rule rule : rules) {
                if (StringUtil.isEmpty(rule.getWhiteName()) || rule.getWhiteId() == null) {
                    throw new RuntimeException(ProductProfitEnum.白名单.getName() + ":商品权益格式错误00");
                }
            }
            return rules;
        } catch (Exception e) {
            throw new RuntimeException(ProductProfitEnum.白名单.getName() + ":商品权益格式错误1");
        }

    }

    @Override
    public void orderSuccess(Order order, List<OrderDetail> orderDetailList, List<ProductProfit> productProfitList) {
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
            List<Rule> rules = getRule(productProfit.getRule());
            StringBuilder profitPostscript  = new StringBuilder();
            profitPostscript.append("增加白名单");
            for (Rule rule : rules) {
                if (whiteUserService.getByUser(order.getUid(), rule.getWhiteId()) == null) {
                    whiteUserService.add(order.getUid(), rule.getWhiteId(), order.getOrderNo());
                    profitPostscript.append("【").append(rule.getWhiteName()).append("】");
                }
            }
            // 订单权益记录
            orderProductProfitService.save(order.getId(), order.getOrderNo(), productProfit.getProductId(), getType(),
                    ProductProfitEnum.白名单.getName(), JSONArray.toJSONString(rules), profitPostscript.toString());
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
            String context = "购买订单增加【"+ProductProfitEnum.白名单.getName()+"】, 商品名称【" + refundDetail.getProductName() + "】收益名称【" + productProfit.getProfitName() + "】 收益说明【" + productProfit.getPostscript() + "】";
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
         * 白名单ID
         */
        private Long whiteId;

        /**
         * 白名单名称
         */
        private String whiteName;

    }
}
