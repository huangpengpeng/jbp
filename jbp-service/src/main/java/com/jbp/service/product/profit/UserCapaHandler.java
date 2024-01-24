package com.jbp.service.product.profit;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.service.schema.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.model.agent.UserCapa;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.product.Product;
import com.jbp.service.service.OrderProductProfitService;
import com.jbp.service.service.ProductService;
import com.jbp.service.service.agent.ProductProfitConfigService;
import com.jbp.service.service.agent.ProductProfitService;
import com.jbp.service.service.agent.UserCapaService;
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
 * 等级处理器
 */
@Component
public class UserCapaHandler implements ProductProfitHandler {

    @Resource
    private ProductProfitService productProfitService;
    @Resource
    private UserCapaService userCapaService;
    @Resource
    private OrderProductProfitService orderProductProfitService;
    @Resource
    private ProductService productService;
    @Resource
    private ProductProfitConfigService configService;


    @Override
    public Integer getType() {
        return ProductProfitEnum.等级.getType();
    }

    @Override
    public void save(ProductProfit productProfit) {
        getRule(productProfit.getRule());
        productProfitService.remove(new QueryWrapper<ProductProfit>().lambda().eq(ProductProfit::getProductId,
                productProfit.getProductId()).eq(ProductProfit::getType, productProfit.getType()));
        productProfitService.save(productProfit);
    }

    @Override
    public Rule getRule(String ruleStr) {
        try {
            Rule rule = JSONObject.parseObject(ruleStr).toJavaObject(Rule.class);
            if (StringUtil.isEmpty(rule.getName()) || rule.getCapaId() == null) {
                throw new RuntimeException(ProductProfitEnum.等级.getName() + ":商品权益格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(ProductProfitEnum.等级.getName() + ":商品权益格式错误1");
        }
    }

    @Override
    public void orderSuccess(Order order, List<OrderDetail> orderDetailList, List<ProductProfit> productProfitList) {
        ProductProfitConfig profitConfig = configService.getByType(getType());
        if(profitConfig == null || !BooleanUtil.isTrue(profitConfig.getIfOpen())){
            return;
        }
        productProfitList = ListUtils.emptyIfNull(productProfitList).stream().filter(p -> p.getType() == getType() && BooleanUtil.isTrue(p.getStatus())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(productProfitList)) {
            return;
        }
        // 获取执行等级
        Long capaId = -1L;
        ProductProfit exceProductProfit = null;
        for (ProductProfit productProfit : productProfitList) {
            Rule rule = getRule(productProfit.getRule());
            if (NumberUtil.compare(rule.getCapaId(), capaId) > 0) {
                capaId = rule.getCapaId();
                exceProductProfit = productProfit;
            }
        }
        // 当前等级比直升等级大
        UserCapa userCapa = userCapaService.getByUser(order.getUid());
        if (userCapa != null && NumberUtil.compare(userCapa.getCapaId(), capaId) >= 0) {
            return;
        }
        // 产品配置升级信息大于当前用户等级 执行升级
        Product product = productService.getById(exceProductProfit.getProductId());
        userCapaService.saveOrUpdateCapa(order.getUid(), capaId,
                "订单支付成功产品:" + product.getName() + ", 权益设置直升等级", order.getOrderNo());
        // 订单权益记录
        orderProductProfitService.save(order.getId(), order.getOrderNo(), product.getId(), getType(),
                ProductProfitEnum.等级.getName(), exceProductProfit.getRule());
    }

    @Override
    public void orderRefund(Order order) {
//        List<OrderProductProfit> orderProductProfits = orderProductProfitService.getByOrder(order.getId(), getType(),
//                OrderProductProfit.Constants.成功.name());
//        if (CollectionUtils.isEmpty(orderProductProfits)) {
//            return;
//        }
//        OrderProductProfit profit = orderProductProfits.get(0);
//        Rule rule = getRule(profit.getRule());
//        OrderExt orderExt = orderExtService.getByOrder(order.getId());
//        UserCapa userCapa = userCapaService.getByUser(order.getUid());
//        // 没有等级退出
//        if (userCapa == null) {
//            return;
//        }
//        // 下单后没有等级退出
//        if (orderExt.getSuccessCapaId() == null) {
//            return;
//        }
//        // 下单前后等级一样退出
//        if (orderExt.getCapaId() != null && NumberUtil.compare(orderExt.getCapaId(), orderExt.getSuccessCapaId()) == 0) {
//            return;
//        }
//        // 下单成功后等级不是规则等级
//        if (NumberUtil.compare(orderExt.getSuccessCapaId(), rule.getCapaId()) != 0) {
//            return;
//        }
//        // 当前等级不是规则等级，说明其他订单动了等级退出不处理
//        if (NumberUtil.compare(userCapa.getCapaId(), rule.getCapaId()) != 0) {
//            return;
//        }
//        Product product = productService.getById(profit.getProductId());
//        userCapaService.saveOrUpdateCapa(order.getUid(), orderExt.getCapaId(),
//                "订单退款成功产品:" + product.getName() + ", 权益直升等级回退", order.getOrderNo());
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
         * 升级等级名称
         */
        private String name;

        /**
         * 升级等级
         */
        private Long capaId;
    }
}
