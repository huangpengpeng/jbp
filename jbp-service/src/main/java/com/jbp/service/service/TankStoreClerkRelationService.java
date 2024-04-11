package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.tank.TankStoreClerkRelation;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.TankOrderAdminListResponse;
import com.jbp.common.response.TankStoreClerkAdminListResponse;
import com.jbp.common.response.TankStoreClerkManagerListResponse;

import java.util.List;

public interface TankStoreClerkRelationService extends IService<TankStoreClerkRelation> {



    TankStoreClerkRelation getClerkUserId(Integer clerkUserId);

    List<TankStoreClerkRelation> getStoreUserId(Long storeUserId);


    List<TankStoreClerkRelation> getStoreId(Long storeId);


    List<TankStoreClerkManagerListResponse> getClerkList();


    PageInfo<TankStoreClerkAdminListResponse> getAdminPageList(String username, String name, PageParamRequest pageParamRequest);




}
