package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.tank.TankStoreRelation;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.TankStoreClerkAdminListResponse;
import com.jbp.common.response.TankStoreRelationAdminListResponse;
import com.jbp.common.response.TankStoreRelationListResponse;

import java.util.List;

public interface TankStoreRelationService  extends IService<TankStoreRelation> {


    TankStoreRelation getStoreUserId(Long userId);

    List<TankStoreRelation> getTankUserId(Integer userId);

    List<TankStoreRelationListResponse> getRelationList();


    PageInfo<TankStoreRelationAdminListResponse> getAdminPageList(String username, String storeusername, PageParamRequest pageParamRequest);


}
