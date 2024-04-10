package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.tank.TankStore;

import java.util.List;

public interface TankStoreService  extends IService<TankStore> {

    TankStore getName(String name);
    public List<TankStore> getStoreUserId(Long userId);

}
