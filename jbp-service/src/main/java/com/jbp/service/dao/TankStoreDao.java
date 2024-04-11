package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.tank.TankStore;
import com.jbp.common.response.TankStoreAdminListResponse;
import com.jbp.common.response.TankStoreListResponse;
import com.jbp.common.response.TankStoreManageListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TankStoreDao  extends BaseMapper<TankStore> {


    List<TankStoreListResponse> getStoreList(Integer storeUserId);


    List<TankStoreManageListResponse> getStoreManageList(Integer userId);

    List<TankStoreAdminListResponse> getAdminPageList(@Param("username")String username, @Param("name")String name);

}
