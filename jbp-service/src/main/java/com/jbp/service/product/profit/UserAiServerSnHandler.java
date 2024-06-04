package com.jbp.service.product.profit;

import cn.hutool.core.util.BooleanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jbp.common.model.agent.ProductProfit;
import com.jbp.common.model.agent.ProductProfitConfig;
import com.jbp.common.model.agent.Team;
import com.jbp.common.model.agent.TeamUser;
import com.jbp.common.model.order.Order;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.order.OrderExt;
import com.jbp.common.model.order.RefundOrder;
import com.jbp.service.service.OrderExtService;
import com.jbp.service.service.TeamService;
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
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 服务码处理器
 */
@Component
public class UserAiServerSnHandler implements ProductProfitHandler {

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
    @Resource
    private TeamService teamService;

    @Override
    public Integer getType() {
        return ProductProfitEnum.AI服务码.getType();
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
            UserAiServerSnHandler.Rule rule = JSONObject.parseObject(ruleStr).toJavaObject(UserAiServerSnHandler.Rule.class);
            if (rule.getNum() == null ||rule.getDay() == null ) {
                throw new RuntimeException(ProductProfitEnum.AI服务码.getName() + ":商品权益格式错误0");
            }
            return rule;
        } catch (Exception e) {
            throw new RuntimeException(ProductProfitEnum.AI服务码.getName() + ":商品权益格式错误1");
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
            Integer teamId = 0;
            if(teamUser != null){
                Team team =  teamService.getById( teamUser.getTid());
                if(team!= null){
                    teamId = team.getLeaderId();
                }
            }

            String serverSns = "AI"+ order.getId() + new Random().nextInt(1000) + i;
            serverSn = serverSn + "," + serverSns;
        }

        OrderExt orderExt = orderExtService.getByOrder(order.getOrderNo());
        orderExt.setAiServerSn(serverSn);
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

        private Integer day;

    }


}
