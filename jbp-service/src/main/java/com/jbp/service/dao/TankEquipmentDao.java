package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.tank.TankEquipment;
import com.jbp.common.response.ActivateInfoResponse;
import com.jbp.common.response.EquipmentAdminListResponse;
import com.jbp.common.response.EquipmentListResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TankEquipmentDao extends BaseMapper<TankEquipment> {



    List<EquipmentListResponse> getPageList(@Param("userId") Integer  userId,@Param("type") String type);


    Map<String,Object> getInfo(String equipmentSn);


    Integer equipmentNumber(Integer userId);

    Integer equipmentUseNumber(Integer userId);

    Integer equipmentOnlineUnusedNumber(Integer userId);

    Integer equipmentOfflinedNumber(Integer userId);



    List<EquipmentAdminListResponse> getAdminPageList(@Param("username") String  username, @Param("name") String name);


}
