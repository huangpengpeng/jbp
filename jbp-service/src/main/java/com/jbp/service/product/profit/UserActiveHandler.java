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
import com.jbp.common.model.user.User;
import com.jbp.common.utils.DateTimeUtils;
import com.jbp.service.service.OrderProductProfitService;
import com.jbp.service.service.UserService;
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
        Date newActiveTime = DateTimeUtils.getFinallyDate(DateTimeUtils.getNow());
        ProductProfit exceProductProfit = null;
        for (ProductProfit productProfit : productProfitList) {
            Rule rule = getRule(productProfit.getRule());
            Date activeTime = DateTimeUtils.getFinallyDate(DateTimeUtils.getNow());
            int value = rule.getValue() - 1;
            if (value > 0) {
                activeTime = "month".equals(rule.getUnit()) ? DateTimeUtils.addMonths(activeTime, value) : DateTimeUtils.addDays(activeTime, value);
                activeTime = DateTimeUtils.getFinallyDate(activeTime);
            }
            if (activeTime.after(newActiveTime)) {
                newActiveTime = activeTime;
                exceProductProfit = productProfit;
            }
        }
        Date activeTime = user.getActiveTime();
        if (user.getActiveTime() != null && user.getActiveTime().after(newActiveTime)) {
            return;
        }
        user.setActiveTime(newActiveTime);
        userService.updateById(user);
        // 订单权益记录
        OrderProductProfit profit = orderProductProfitService.save(order.getId(), order.getOrderNo(), exceProductProfit.getProductId(), getType(),
                ProductProfitEnum.活跃.getName(), exceProductProfit.getRule());
        profit.setRemark("下单前活跃到期:" + activeTime == null ? "" : DateTimeUtils.format(activeTime, DateTimeUtils.DEFAULT_DATE_TIME_FORMAT_PATTERN));
        orderProductProfitService.updateById(profit);
    }

    @Override
    public void orderRefund(Order order) {
        // 订单退款不处理活跃  连续下单的情况

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
