package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.tank.TankStoreRelation;

import java.util.List;

public interface TankStoreRelationService  extends IService<TankStoreRelation> {


    TankStoreRelation getStoreUserId(Long userId);

    List<TankStoreRelation> getTankUserId(Long userId);


}
