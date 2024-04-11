package com.jbp.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.jbp.common.model.tank.TankActivate;
import com.jbp.common.model.user.UserIntegralRecord;
import com.jbp.common.request.PageParamRequest;
import com.jbp.common.response.ActivateAdminListResponse;
import com.jbp.common.response.ActivateInfoResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TankActivateService extends IService<TankActivate> {


    Boolean activateEquipment(Integer worktime, Long equipment_id, String token);


    Integer activateDay(Long storeId);

    Integer activateWeeks(Long storeId);

    Integer activateMonth(Long storeId);

    Integer activateTotal(Long storeId);

    List<Integer> activateRecent(Long storeId);


    PageInfo<ActivateInfoResponse> getactivateList(PageParamRequest pageParamRequest);


    PageInfo<ActivateAdminListResponse> getadminActivateList(String username,  String name,  String status, PageParamRequest pageParamRequest);


}
