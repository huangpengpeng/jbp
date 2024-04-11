package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.model.tank.TankActivate;
import com.jbp.common.response.ActivateAdminListResponse;
import com.jbp.common.response.ActivateInfoResponse;
import com.jbp.common.response.SeckillProductPageResponse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TankActivateDao  extends BaseMapper<TankActivate> {


   Integer activateDay(Long storeId);

    Integer activateWeeks(Long storeId);
    Integer activateMonth(Long storeId);

    Integer activateTotal(Long storeId);

    List<Integer> activateRecent(Long storeId);


    List<ActivateInfoResponse> getactivateList(@Param("userId") Integer userId);

    List<ActivateAdminListResponse> getadminActivateList(@Param("username") String username,@Param("name") String name,@Param("status") String status);


}
