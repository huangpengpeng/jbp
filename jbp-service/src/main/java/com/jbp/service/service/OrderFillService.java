package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.order.OrderFill;
import com.jbp.common.request.PageParamRequest;


public interface OrderFillService extends IService<OrderFill> {
    PageInfo<OrderFill> getList(Integer uid, String oNickname, String orderNo, PageParamRequest pageParamRequest);
}
