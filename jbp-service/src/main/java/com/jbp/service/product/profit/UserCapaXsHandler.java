package com.jbp.service.product.profit;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.service.schema.util.StringUtil;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderExt;
import com.jbp.common.model.order.OrderProductProfit;
import com.jbp.common.model.product.Product;
import com.jbp.common.model.product.ProductProfit;
import com.jbp.service.service.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 星级处理器
 */
@Component
public class UserCapaXsHandler implements ProductProfitHandler {

    @Resource
    private ProductProfitService productProfitService;
    @Resource
    private UserCapaXsService userCapaXsService;
    @Resource
    private OrderProductProfitService orderProductProfitService;
    @Resource
    private OrderExtService orderExtService;
    @Resource
    private ProductService productService;


    @Override
    public Integer getType() {
        return ProductProfitEnum.星级.getType();
    }

    @Override
    public void save(ProductProfit productProfit) {
        getRule(productProfit.getRule());
        productProfitService.save(productProfit);
    }

    @Override
    public Rule getRule(String ruleStr) {
        try {
            Rule rule = JSONObject.parseObject(ruleStr).toJavaObject(Rule.class);
            if (StringUtil.isEmpty(rule.getName()) || rule.getCapaXsId() == null) {
                throw new RuntimeException(ProductProfitEnum.星级.getName() + ":商品权益格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(ProductProfitEnum.星级.getName() + ":商品权益格式错误1");
        }
    }

    @Override
    public void orderSuccess(Order order, List<OrderDetail> orderDetailList, List<ProductProfit> productProfitList) {
        productProfitList = ListUtils.emptyIfNull(productProfitList).stream().filter(p -> p.getType() == getType()).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(productProfitList)) {
            return;
        }
        UserCapaXs userCapaXs = userCapaXsService.getByUser(order.getUid());
        Long capaXsId = -1L;
        Integer productId = null;
        String productName = null;
        String ruleStr = "";
        for (ProductProfit productProfit : productProfitList) {
            Rule rule = getRule(productProfit.getRule());
            if (NumberUtil.compare(rule.getCapaXsId(), capaXsId) > 0) {
                capaXsId = rule.getCapaXsId();
                productId = productProfit.getProductId();
                productName = rule.getName();
                ruleStr = productProfit.getRule();
            }
        }
        // 产品配置升级信息大于当前用户等级 执行升级
        if (NumberUtil.compare(capaXsId, userCapaXs == null ? 0L : userCapaXs.getCapaId()) > 0) {
            userCapaXsService.saveOrUpdateCapa(order.getUid(), capaXsId,
                    "订单支付成功产品:" + productName + ", 权益设置直升星级", order.getOrderNo());
            // 订单权益记录
            orderProductProfitService.save(order.getId(), order.getOrderNo(), productId, getType(),
                    ProductProfitEnum.星级.getName(), ruleStr);
        }
    }

    @Override
    public void orderRefund(Order order) {
//        List<OrderProductProfit> orderProductProfits = orderProductProfitService.getByOrder(order.getId(), getType(), OrderProductProfit.Constants.成功.name());
//        if (CollectionUtils.isEmpty(orderProductProfits)) {
//            return;
//        }
//        OrderProductProfit profit = orderProductProfits.get(0);
//        Rule rule = getRule(profit.getRule());
//        OrderExt orderExt = orderExtService.getByOrder(order.getId());
//        UserCapaXs userCasaXs = userCapaXsService.getByUser(order.getUid());
//        // 没有星级退出
//        if (userCasaXs == null) {
//            return;
//        }
//        // 下单后没星级退出
//        if (orderExt.getSuccessCapaXsId() == null) {
//            return;
//        }
//        // 下单前后星级一样退出
//        if (orderExt.getCapaXsId() != null && NumberUtil.compare(orderExt.getCapaXsId(), orderExt.getSuccessCapaXsId()) == 0) {
//            return;
//        }
//        // 下单后的星级不是规则直升星级退出
//        if (NumberUtil.compare(orderExt.getSuccessCapaXsId(), rule.getCapaXsId()) != 0) {
//            return;
//        }
//        // 当前用户星级不等于设置的直升星级退出
//        if (NumberUtil.compare(userCasaXs.getCapaId(), rule.getCapaXsId()) != 0) {
//            return;
//        }
//        Product product = productService.getById(profit.getProductId());
//        userCapaXsService.saveOrUpdateCapa(order.getUid(), orderExt.getCapaXsId(),
//                "订单退款产品:" + product.getName() + ", 权益直升星级回退", order.getOrderNo());
//        profit.setStatus(OrderProductProfit.Constants.退回.name());
//        orderProductProfitService.updateById(profit);
    }

    /**
     * 当前权益对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /**
         * 升级星级名称
         */
        private String name;

        /**
         * 升级星级
         */
        private Long capaXsId;
    }
}
