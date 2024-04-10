package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.tank.TankStoreRelation;
import com.jbp.common.response.TankStoreRelationListResponse;

import java.util.List;

public interface TankStoreRelationDao  extends BaseMapper<TankStoreRelation> {


    public List<TankStoreRelationListResponse> getRelationList(Integer userId);
}
