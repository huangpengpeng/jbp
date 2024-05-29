package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.order.Order;

import com.jbp.common.model.order.OrderDetail;
import com.jbp.common.response.OrderSalesVolumeResponse;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-09-19
 */
public interface OrderDao extends BaseMapper<Order> {

    /**
     * 获取用户购买的商品数量
     * @param uid 用户ID
     * @param proId 商品ID
     * @param productType 商品类型
     */
    Integer getProductNumCount(@Param(value = "uid") Integer uid, @Param(value = "proId") Integer proId, @Param(value = "productType") Integer productType);

    /**
     * 获取移动端订单列表
     * @param searchMap 搜索参数
     */
    List<Order> findFrontList(Map<String, Object> searchMap);

    BigDecimal getGoodsPirce(@Param(value = "goodsIds") String goodsIds, @Param(value = "uid") Integer uid, @Param(value = "month") String month);


    List<Map<String,Object>> getFgGoodsOrder(@Param(value = "goodsIds") String goodsIds, @Param(value = "month") String month);


    List<OrderSalesVolumeResponse> salesVolumeDay();
}
