package com.jbp.service.product.profit;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.LotteryUser;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderExt;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.service.service.OrderExtService;
import com.jbp.service.service.TeamService;
import com.jbp.service.service.TeamUserService;
import com.jbp.service.service.agent.LotteryUserService;
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
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 抽奖处理器
 */
@Component
public class UserPrizeServerSnHandler implements ProductProfitHandler {

    @Resource
    private ProductProfitService productProfitService;
    @Resource
    private OrderExtService orderExtService;
    @Resource
    private LotteryUserService lotteryUserService;
    @Resource
    private ProductProfitConfigService configService;
    @Autowired
    private Environment environment;
    @Resource
    private TeamService teamService;

    @Override
    public Integer getType() {
        return ProductProfitEnum.抽奖次数.getType();
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
            UserPrizeServerSnHandler.Rule rule = JSONObject.parseObject(ruleStr).toJavaObject(UserPrizeServerSnHandler.Rule.class);
            if (rule.getLotteryId() == null ||rule.getNumber() == null ) {
                throw new RuntimeException(ProductProfitEnum.抽奖次数.getName() + ":商品权益格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(ProductProfitEnum.抽奖次数.getName() + ":商品权益格式错误1");
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

        //赠送抽奖次数
        lotteryUserService.increase(order.getUid(),rule.getLotteryId().longValue(),rule.getNumber());

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

        private Integer lotteryId;

        private Integer number;

    }


}
