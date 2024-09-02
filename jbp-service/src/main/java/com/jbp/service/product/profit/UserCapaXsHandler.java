package com.jbp.service.product.profit;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.service.schema.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.model.agent.UserCapaXs;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderProductProfit;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.common.model.product.Product;
import com.jbp.common.response.RefundOrderInfoResponse;
import com.jbp.service.service.OrderProductProfitService;
import com.jbp.service.service.ProductService;
import com.jbp.service.service.RefundOrderService;
import com.jbp.service.service.agent.OrdersRefundMsgService;
import com.jbp.service.service.agent.ProductProfitConfigService;
import com.jbp.service.service.agent.ProductProfitService;
import com.jbp.service.service.agent.UserCapaXsService;
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
    private ProductService productService;
    @Resource
    private ProductProfitConfigService configService;
    @Resource
    private OrdersRefundMsgService refundMsgService;
    @Resource
    private RefundOrderService refundOrderService;


    @Override
    public Integer getType() {
        return ProductProfitEnum.星级.getType();
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
        ProductProfitConfig profitConfig = configService.getByType(getType());
        if (profitConfig == null || !BooleanUtil.isTrue(profitConfig.getIfOpen())) {
            return;
        }
        productProfitList = ListUtils.emptyIfNull(productProfitList).stream().filter(p -> p.getType() == getType()
                && BooleanUtil.isTrue(p.getStatus())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(productProfitList)) {
            return;
        }

        if(order.getPlatform().equals("订货")){
            return;
        }


        // 获取执行等级
        Long capaXsId = -1L;
        ProductProfit exceProductProfit = null;
        for (ProductProfit productProfit : productProfitList) {
            Rule rule = getRule(productProfit.getRule());
            if (NumberUtil.compare(rule.getCapaXsId(), capaXsId) > 0) {
                capaXsId = rule.getCapaXsId();
                exceProductProfit = productProfit;
            }
        }

        // 当前星级级比直升等级大
        UserCapaXs userCapaXs = userCapaXsService.getByUser(order.getUid());
        if (userCapaXs != null && NumberUtil.compare(userCapaXs.getCapaId(), capaXsId) >= 0) {
            return;
        }
        String oldCapaName = userCapaXs == null ? "" : userCapaXs.getCapaName();
        // 产品配置升级信息大于当前用户等级 执行升级
        Product product = productService.getById(exceProductProfit.getProductId());
        // 产品配置升级信息大于当前用户等级 执行升级
        userCapaXsService.saveOrUpdateCapa(order.getUid(), capaXsId, false,
                "订单支付成功产品:" + product.getName() + ", 权益设置直升星级", order.getOrderNo());
        userCapaXs = userCapaXsService.getByUser(order.getUid());
        String newCapaName = userCapaXs == null ? "" : userCapaXs.getCapaName();
        // 订单权益记录
        StringBuilder profitPostscript  = new StringBuilder();
        profitPostscript.append("下单前星级【"+oldCapaName+"】").append("下单后星级【"+newCapaName+"】");
        orderProductProfitService.save(order.getId(), order.getOrderNo(), product.getId(), getType(),
                ProductProfitEnum.星级.getName(), exceProductProfit.getRule(), profitPostscript.toString());
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
            String context = "购买订单增加【" + ProductProfitEnum.星级.getName() + "】, 商品名称【" + refundDetail.getProductName() + "】收益名称【" + productProfit.getProfitName() + "】收益说明【" + productProfit.getPostscript() + "】";
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
         * 升级星级名称
         */
        private String name;

        /**
         * 升级星级
         */
        private Long capaXsId;
    }
}
