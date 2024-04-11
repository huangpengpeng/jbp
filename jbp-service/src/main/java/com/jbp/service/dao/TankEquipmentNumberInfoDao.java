package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.tank.TankEquipmentNumberInfo;
import com.jbp.common.response.EquipmentNumberAdminListResponse;
import com.jbp.common.response.EquipmentNumberInfoAdminListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TankEquipmentNumberInfoDao extends BaseMapper<TankEquipmentNumberInfo> {


    List<EquipmentNumberInfoAdminListResponse> getAdminPageList(@Param("id") Integer  id);

}
