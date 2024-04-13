package com.jbp.service.product.profit;

import cn.hutool.core.util.BooleanUtil;
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
    public <T> T getRule(String rule) {
        return null;
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

        ProductProfit exceProductProfit = productProfitList.get(0);
        Integer number = Integer.valueOf(exceProductProfit.getRule());

        String serverSn = "";
        for (int i = 0; i < number; i++) {

            TeamUser teamUser = teamUserService.getByUser(order.getUid());

            String channel = environment.getProperty("spring.profiles.active");
            String serverSns = order.getOrderNo() + "_" + channel + "_" + teamUser.getTid() + "_" + i;
            serverSn = serverSns + "," + serverSns;
        }

        OrderExt orderExt = orderExtService.getByOrder(order.getOrderNo());
        orderExt.setServerSn(serverSn);
        orderExtService.updateById(orderExt);

    }

    @Override
    public void orderRefund(Order order, RefundOrder refundOrder) {

    }


}
