package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.order.OrderExt;
import com.jbp.common.model.user.User;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.request.agent.OrderExtProductListRequest;
import com.jbp.common.response.OrderExtProductResponse;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;

public interface OrderExtService extends IService<OrderExt> {

    OrderExt getByOrder(String orderNo);

    Map<String, OrderExt> getOrderNoMapList(List<String> orderNoList);

    PageInfo<OrderExtProductResponse> getProductPage(OrderExtProductListRequest request, PageParamRequest pageParamRequest);
}
