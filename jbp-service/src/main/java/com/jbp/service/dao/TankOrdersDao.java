package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.tank.TankOrders;
import com.jbp.common.response.EquipmentNumberInfoAdminListResponse;
import com.jbp.common.response.TankOrderAdminListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TankOrdersDao extends BaseMapper<TankOrders> {


    List<TankOrderAdminListResponse> getAdminPageList(@Param("username")String username, @Param("status")String status, @Param("startCreateTime") String startCreateTime, @Param("endCreateTime") String endCreateTime);

}
