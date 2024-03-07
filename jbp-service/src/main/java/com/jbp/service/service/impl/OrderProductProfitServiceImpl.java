package com.jbp.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.order.OrderProductProfit;
import com.jbp.common.page.CommonPage;
import com.jbp.common.request.PageParamRequest;
import com.jbp.service.dao.OrderProductProfitDao;
import com.jbp.service.service.OrderProductProfitService;
import com.jbp.service.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderProductProfitServiceImpl extends ServiceImpl<OrderProductProfitDao, OrderProductProfit> implements OrderProductProfitService {
    @Override
    public OrderProductProfit save(Integer orderId, String orderNo, Integer productId, Integer profitType, String profitName, String rule) {
        OrderProductProfit profit = new OrderProductProfit(orderId, orderNo, productId, profitType, profitName, rule);
        save(profit);
        return profit;
    }

    @Override
    public List<OrderProductProfit> getByOrder(Integer orderId, Integer profitType, String status) {
        return list(new QueryWrapper<OrderProductProfit>().lambda()
                .eq(OrderProductProfit::getOrderId, orderId)
                .eq(OrderProductProfit::getProfitType, profitType)
                .eq(OrderProductProfit::getStatus, status));
    }

    @Override
    public PageInfo<OrderProductProfit> pageList(String orderNo, String profitName, String status, PageParamRequest pageParamRequest) {
        LambdaQueryWrapper<OrderProductProfit> lqw = new LambdaQueryWrapper<OrderProductProfit>()
                .like(StringUtils.isNotEmpty(orderNo), OrderProductProfit::getOrderNo, orderNo)
                .like(StringUtils.isNotEmpty(profitName), OrderProductProfit::getProfitName, profitName)
                .like(StringUtils.isNotEmpty(status), OrderProductProfit::getStatus, status);
        Page<OrderProductProfit> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return CommonPage.copyPageInfo(page, list(lqw));
    }
}
