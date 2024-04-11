package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.tank.TankOrders;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.EquipmentNumberInfoAdminListResponse;
import com.jbp.common.response.TankOrderAdminListResponse;

public interface TankOrdersService  extends IService<TankOrders> {

    TankOrders getOrderSn(String orderSn);


    PageInfo<TankOrderAdminListResponse> getAdminPageList(String username, String status, String startCreateTime, String endCreateTime, PageParamRequest pageParamRequest);
}
