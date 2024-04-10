package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.tank.TankEquipmentNumberInfo;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.EquipmentListResponse;
import com.jbp.common.response.EquipmentNumberInfoResponse;

public interface TankEquipmentNumberInfoService extends IService<TankEquipmentNumberInfo> {

    PageInfo<EquipmentNumberInfoResponse> getPageList(String type, PageParamRequest pageParamRequest);


}
