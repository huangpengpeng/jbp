package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.order.OrderProductProfit;
import com.jbp.common.request.PageParamRequest;

import java.util.List;

public interface OrderProductProfitService extends IService<OrderProductProfit> {

    OrderProductProfit save(Integer orderId, String orderNo, Integer productId, Integer profitType, String profitName, String rule, String postscript);

    List<OrderProductProfit> getByOrder(String orderNo, Integer profitType, String status);

    PageInfo<OrderProductProfit> pageList(String orderNo, String profitName, String status, PageParamRequest pageParamRequest);
}
