package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.tank.TankStoreClerkRelation;
import com.jbp.common.response.TankOrderAdminListResponse;
import com.jbp.common.response.TankStoreClerkAdminListResponse;
import com.jbp.common.response.TankStoreClerkManagerListResponse;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TankStoreClerkRelationDao extends BaseMapper<TankStoreClerkRelation> {


    public List<TankStoreClerkManagerListResponse> getClerkList(Integer userId);


    List<TankStoreClerkAdminListResponse> getAdminPageList(@Param("username")String username, @Param("name")String name);

}
