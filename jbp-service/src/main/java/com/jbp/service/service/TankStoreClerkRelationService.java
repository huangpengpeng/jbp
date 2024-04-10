package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jbp.common.model.tank.TankStoreClerkRelation;
import com.jbp.common.response.TankStoreClerkManagerListResponse;

import java.util.List;

public interface TankStoreClerkRelationService extends IService<TankStoreClerkRelation> {



    TankStoreClerkRelation getClerkUserId(Integer clerkUserId);

    List<TankStoreClerkRelation> getStoreUserId(Long storeUserId);


    List<TankStoreClerkRelation> getStoreId(Long storeId);


    List<TankStoreClerkManagerListResponse> getClerkList();


}
