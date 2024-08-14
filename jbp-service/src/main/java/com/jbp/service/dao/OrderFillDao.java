package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.order.OrderFill;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface OrderFillDao extends BaseMapper<OrderFill> {

    List<OrderFill> getList(@Param("uid") Integer uid, @Param("oNickname") String oNickname, @Param("orderNo")String orderNo);
}
