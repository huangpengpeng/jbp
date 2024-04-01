package com.jbp.service.product.profit;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.service.schema.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderProductProfit;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.common.model.user.User;
import com.jbp.common.response.RefundOrderInfoResponse;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.service.OrderProductProfitService;
import com.jbp.service.service.RefundOrderService;
import com.jbp.service.service.UserService;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户活跃处理器
 */
@Component
public class UserActiveHandler implements ProductProfitHandler {

    @Resource
    private ProductProfitService productProfitService;
    @Resource
    private OrderProductProfitService orderProductProfitService;
    @Resource
    private ProductProfitConfigService configService;
    @Resource
    private UserService userService;
    @Resource
    private OrdersRefundMsgService refundMsgService;
    @Resource
    private RefundOrderService refundOrderService;

    @Override
    public Integer getType() {
        return ProductProfitEnum.活跃.getType();
    }

    @Override
    public void save(ProductProfit productProfit) {
        getRule(productProfit.getRule());
        productProfitService.remove(new QueryWrapper<ProductProfit>().lambda().eq(ProductProfit::getProductId,
                productProfit.getProductId()).eq(ProductProfit::getType, productProfit.getType()));
        productProfitService.save(productProfit);
    }

    @Override
    public Rule  getRule(String ruleStr) {
        try {
            Rule rule = JSONObject.parseObject(ruleStr).toJavaObject(UserActiveHandler.Rule.class);
            if (StringUtil.isEmpty(rule.getUnit()) || rule.getValue() == null) {
                throw new RuntimeException(ProductProfitEnum.活跃.getName() + ":商品权益格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(ProductProfitEnum.活跃.getName() + ":商品权益格式错误1");
        }
    }

    @Override
    public void orderSuccess(Order order, List<OrderDetail> orderDetailList, List<ProductProfit> productProfitList) {
        ProductProfitConfig profitConfig = configService.getByType(getType());
        if (profitConfig == null || !BooleanUtil.isTrue(profitConfig.getIfOpen())) {
            return;
        }

        productProfitList = ListUtils.emptyIfNull(productProfitList).stream().filter(p -> p.getType() == getType() && BooleanUtil.isTrue(p.getStatus())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(productProfitList)) {
            return;
        }
        User user = userService.getById(order.getUid());
        Date now = DateTimeUtils.getNow();
        Date newActiveTime = DateTimeUtils.getNow();
        ProductProfit exceProductProfit = null;
        for (ProductProfit productProfit : productProfitList) {
            Rule rule = getRule(productProfit.getRule());
            int value = rule.getValue();
            Date activeTime = "月".equals(rule.getUnit()) ? DateTimeUtils.getMonthEnd(DateTimeUtils.addMonths(now, value)) : DateTimeUtils.getFinallyDate(DateTimeUtils.addDays(now, value));
            if (activeTime.after(newActiveTime)) {
                newActiveTime = activeTime;
                exceProductProfit = productProfit;
            }
        }
        // 活跃规则
        Rule rule = getRule(exceProductProfit.getRule());
        // 不活跃 当前月份活跃
        Date activeTime = user.getActiveTime();
        if (user.getActiveTime() == null || user.getActiveTime().before(DateTimeUtils.getNow())) {
            if ("月".equals(rule.getUnit())) {
                newActiveTime = DateTimeUtils.getMonthEnd(DateTimeUtils.addMonths(DateTimeUtils.getNow(), rule.getValue() - 1));
            } else {
                newActiveTime = DateTimeUtils.getFinallyDate(DateTimeUtils.addDays(DateTimeUtils.getNow(), rule.getValue() - 1));
            }
        } else {
            if ("月".equals(rule.getUnit())) {
                newActiveTime = DateTimeUtils.getMonthEnd(DateTimeUtils.addMonths(user.getActiveTime(), rule.getValue() - 1));
            } else {
                newActiveTime = DateTimeUtils.getFinallyDate(DateTimeUtils.addDays(user.getActiveTime(), rule.getValue() - 1));
            }
        }
        user.setActiveTime(newActiveTime);
        userService.updateById(user);
        String oldActiveTimeStr = activeTime == null ? "" : DateTimeUtils.format(activeTime, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN);
        String newActiveTimeStr = DateTimeUtils.format(newActiveTime, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN);
        StringBuilder profitPostscript  = new StringBuilder();
        profitPostscript.append("下单前活跃到期【"+oldActiveTimeStr+"】").append("下单后活跃到期【"+newActiveTimeStr+"】");
        // 订单权益记录
        orderProductProfitService.save(order.getId(), order.getOrderNo(), exceProductProfit.getProductId(), getType(),
                ProductProfitEnum.活跃.getName(), exceProductProfit.getRule(), profitPostscript.toString());
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
        for (OrderProductProfit productProfit : list) {
            String context = "购买订单增加【" + ProductProfitEnum.活跃.getName() + "】, 商品名称【" + refundDetail.getProductName() + "】收益名称【" + productProfit.getProfitName() + "】收益说明【" + productProfit.getPostscript() + "】";
            refundMsgService.create(orderNo, refundOrder.getRefundOrderNo(), context);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /**
         * 单位  day   month
         */
        private String unit;

        /**
         * 值
         */
        private Integer value;
    }
}
