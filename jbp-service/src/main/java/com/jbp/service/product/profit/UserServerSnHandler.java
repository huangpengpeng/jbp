package com.jbp.service.product.profit;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.service.schema.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderExt;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.service.service.OrderExtService;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.agent.ProductProfitConfigService;
import com.jbp.service.service.agent.ProductProfitService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务码处理器
 */
@Component
public class UserServerSnHandler implements ProductProfitHandler {

    @Resource
    private ProductProfitService productProfitService;
    @Resource
    private OrderExtService orderExtService;
    @Resource
    private TeamUserService teamUserService;
    @Resource
    private ProductProfitConfigService configService;
    @Autowired
    private Environment environment;

    @Override
    public Integer getType() {
        return ProductProfitEnum.服务码.getType();
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
            UserServerSnHandler.Rule rule = JSONObject.parseObject(ruleStr).toJavaObject(UserServerSnHandler.Rule.class);
            if (rule.getNum() == null) {
                throw new RuntimeException(ProductProfitEnum.服务码.getName() + ":商品权益格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(ProductProfitEnum.服务码.getName() + ":商品权益格式错误1");
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

        if (CollectionUtils.isEmpty(productProfitList) || !BooleanUtil.isTrue(productProfitList.get(0).getStatus()) ) {
            return;
        }
        Rule rule  =  getRule(productProfitList.get(0).getRule());
        String serverSn = "";
        for (int i = 0; i < rule.getNum(); i++) {

            TeamUser teamUser = teamUserService.getByUser(order.getUid());

            String channel = environment.getProperty("spring.profiles.active");
            String serverSns = order.getOrderNo() + "_" + channel + "_" + teamUser == null ? "0" : teamUser.getTid() + "_" + i;
            serverSn = serverSn + "," + serverSns;
        }

        OrderExt orderExt = orderExtService.getByOrder(order.getOrderNo());
        orderExt.setServerSn(serverSn);
        orderExtService.updateById(orderExt);

    }

    @Override
    public void orderRefund(Order order, RefundOrder refundOrder) {

    }


    /**
     * 当前权益对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Rule {

        private Integer num;

    }


}
