package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.tank.TankEquipment;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.ActivateInfoResponse;
import com.jbp.common.response.EquipmentAdminListResponse;
import com.jbp.common.response.EquipmentListResponse;

import java.util.List;
import java.util.Map;

public interface TankEquipmentService extends IService<TankEquipment> {


    TankEquipment getEquipmentSn(Long equipmentSn);


    List<TankEquipment> getStoreId(Long storeId);


    PageInfo<EquipmentListResponse> getPageList(String type,PageParamRequest pageParamRequest);


    Map<String,Object> getInfo(String equipmentSn);


    Integer equipmentNumber();

    Integer equipmentUseNumber();

    Integer equipmentOnlineUnusedNumber();

    Integer equipmentOfflinedNumber();

    PageInfo<EquipmentAdminListResponse> getAdminPageList(String username,String name, PageParamRequest pageParamRequest);


}
