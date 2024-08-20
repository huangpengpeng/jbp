package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.order.OrderFill;
import com.jbp.common.request.PageParamRequest;

import java.util.List;
import java.util.Map;


public interface OrderFillService extends IService<OrderFill> {



    OrderFill add(String orderNo,Integer uId);


    void saveOrder(String orderNo);

    PageInfo<OrderFill> getList(Integer uid, String oNickname, String orderNo, PageParamRequest pageParamRequest);

    Map<String, OrderFill> getOrderNoMapList(List<String> orderNoList, String status);


    void expired(OrderFill orderFill);

    void fill(OrderFill orderFill);
}
