package com.jbp.service.product.profit;

import com.alibaba.fastjson.JSONObject;
import com.alipay.service.schema.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.service.service.OrderProductProfitService;
import com.jbp.service.service.ProductService;
import com.jbp.service.service.agent.ProductProfitService;
import com.jbp.service.service.agent.UserCapaService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class UserActiveHandler implements ProductProfitHandler {

    @Resource
    private ProductProfitService productProfitService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private OrderProductProfitService orderProductProfitService;
    @Resource
    private ProductService productService;

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

    }

    @Override
    public void orderRefund(Order order) {

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
