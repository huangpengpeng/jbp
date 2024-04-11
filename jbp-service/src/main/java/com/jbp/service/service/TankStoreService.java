package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.tank.TankStore;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.TankOrderAdminListResponse;
import com.jbp.common.response.TankStoreAdminListResponse;
import com.jbp.common.response.TankStoreListResponse;
import com.jbp.common.response.TankStoreManageListResponse;
import io.swagger.models.auth.In;

import java.util.List;

public interface TankStoreService  extends IService<TankStore> {

    TankStore getName(String name);
     List<TankStore> getStoreUserId(Integer userId);

    List<TankStoreListResponse> getStoreList(Integer storeUserId);

    List<TankStoreManageListResponse> getStoreManageList();

    PageInfo<TankStoreAdminListResponse> getAdminPageList(String username, String name, PageParamRequest pageParamRequest);


}
