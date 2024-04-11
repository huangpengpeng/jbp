package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.tank.TankStoreRelation;
import com.jbp.common.response.TankStoreAdminListResponse;
import com.jbp.common.response.TankStoreRelationAdminListResponse;
import com.jbp.common.response.TankStoreRelationListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TankStoreRelationDao  extends BaseMapper<TankStoreRelation> {


    public List<TankStoreRelationListResponse> getRelationList(Integer userId);

    List<TankStoreRelationAdminListResponse> getAdminPageList(@Param("username")String username, @Param("storeusername")String storeusername);

}
