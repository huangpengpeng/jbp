package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.tank.TankEquipmentNumber;

public interface TankEquipmentNumberService  extends IService<TankEquipmentNumber>  {



    TankEquipmentNumber reduce(Long storeUserId, Long userId);

    TankEquipmentNumber getStoreUserId(Long storeUserId);

    TankEquipmentNumber increase(Long storeUserId, Integer number, String orderSn, String remark);
}
