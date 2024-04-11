package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.tank.TankEquipmentNumber;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.EquipmentNumberAdminListResponse;

public interface TankEquipmentNumberService  extends IService<TankEquipmentNumber>  {



    TankEquipmentNumber reduce(Long storeUserId, Long userId);

    TankEquipmentNumber getStoreUserId(Long storeUserId);

    TankEquipmentNumber increase(Long storeUserId, Integer number, String orderSn, String remark);


    PageInfo<EquipmentNumberAdminListResponse> getAdminPageList(String username, PageParamRequest pageParamRequest);
}
