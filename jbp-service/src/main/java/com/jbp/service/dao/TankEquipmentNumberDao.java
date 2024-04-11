package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.tank.TankEquipmentNumber;
import com.jbp.common.response.EquipmentAdminListResponse;
import com.jbp.common.response.EquipmentNumberAdminListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TankEquipmentNumberDao  extends BaseMapper<TankEquipmentNumber> {


    List<EquipmentNumberAdminListResponse> getAdminPageList(@Param("username") String  username);

}
