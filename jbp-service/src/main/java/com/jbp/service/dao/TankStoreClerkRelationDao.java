package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.tank.TankStoreClerkRelation;
import com.jbp.common.response.TankStoreClerkManagerListResponse;
import io.swagger.models.auth.In;

import java.util.List;

public interface TankStoreClerkRelationDao extends BaseMapper<TankStoreClerkRelation> {


    public List<TankStoreClerkManagerListResponse> getClerkList(Integer userId);
}
