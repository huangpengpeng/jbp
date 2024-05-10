package com.jbp.service.product.profit;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.service.schema.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.ClearingVipUser;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderProductProfit;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.common.model.user.User;
import com.jbp.common.response.RefundOrderInfoResponse;
import com.jbp.common.utils.FunctionUtil;
import com.jbp.service.product.comm.PingTaiCommHandler;
import com.jbp.service.product.comm.ProductCommEnum;
import com.jbp.service.service.OrderProductProfitService;
import com.jbp.service.service.RefundOrderService;
import com.jbp.service.service.UserService;
import com.jbp.service.service.agent.ClearingVipUserService;
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
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 赠送结算名单份额
 */
@Component
public class ClearingVipUserHandler implements ProductProfitHandler {

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
    @Resource
    private ClearingVipUserService clearingVipUserService;
    @Resource
    private PingTaiCommHandler pingTaiCommHandler;


    @Override
    public Integer getType() {
        return ProductProfitEnum.平台分红.getType();
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
            if (StringUtil.isEmpty(rule.getLevelName()) || rule.getLevel() == null) {
                throw new RuntimeException(ProductProfitEnum.平台分红.getName() + ":商品权益格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(ProductProfitEnum.平台分红.getName() + ":商品权益格式错误1");
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
        Map<Integer, OrderDetail> detailMap = FunctionUtil.keyValueMap(orderDetailList, OrderDetail::getProductId);
        User user = userService.getById(order.getUid());
        List<PingTaiCommHandler.Rule> commRuleList = pingTaiCommHandler.getRule(null);
        Map<Long, PingTaiCommHandler.Rule> commRuleMap = FunctionUtil.keyValueMap(commRuleList, PingTaiCommHandler.Rule::getLevel);
        // 保存用户权益
        for (ProductProfit productProfit : productProfitList) {
            OrderDetail orderDetail = detailMap.get(productProfit.getProductId());
            Integer payNum = orderDetail.getPayNum();
            Rule rule = getRule(productProfit.getRule());
            PingTaiCommHandler.Rule commRule = commRuleMap.get(rule.getLevel());
            PingTaiCommHandler.Rule orgCommRule = commRuleMap.get(commRule.getRefLevel());
            BigDecimal maxFee = commRule.getMaxFee().multiply(BigDecimal.valueOf(payNum));

            ClearingVipUser clearingVipUser = clearingVipUserService.getByUser(order.getUid(), orgCommRule.getLevel(), ProductCommEnum.平台分红.getType());
            if(clearingVipUser == null){
                clearingVipUserService.create(order.getUid(), user.getAccount(), orgCommRule.getLevel(), orgCommRule.getLevelName(),
                        ProductCommEnum.平台分红.getType(), ProductCommEnum.平台分红.getName(),
                        maxFee, JSONObject.toJSONString(orgCommRule), "单号:" + order.getOrderNo() + "产品:" + orderDetail.getProductName());
            }else{
                clearingVipUser.setMaxAmount(clearingVipUser.getMaxAmount().add(maxFee));
                clearingVipUserService.updateById(clearingVipUser);
            }
            StringBuilder profitPostscript = new StringBuilder();
            profitPostscript.append("增加平台分红名单,等级编号【" + rule.getLevel() + "】").append("等级名称【" + rule.getLevelName() + "】").append("新增额度【"+maxFee+"】");
            orderProductProfitService.save(order.getId(), order.getOrderNo(), productProfit.getProductId(), getType(),
                    ProductProfitEnum.平台分红.getName(), productProfit.getRule(), profitPostscript.toString());
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
        for (OrderProductProfit productProfit : list) {
            String context = "购买订单增加【" + ProductProfitEnum.平台分红.getName() + "】, 商品名称【" + refundDetail.getProductName() + "】收益名称【" + productProfit.getProfitName() + "】收益说明【" + productProfit.getPostscript() + "】";
            refundMsgService.create(orderNo, refundOrder.getRefundOrderNo(), context);
        }
    }

    public static void main(String[] args) {
        Rule rule = new Rule();
        rule.setLevel(1L);
        rule.setLevelName("vip");
        System.out.println(JSONObject.toJSONString(rule));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {
        /**
         * 结算级别
         */
        private Long level;

        /**
         * 结算名称
         */
        private String levelName;

    }
}
