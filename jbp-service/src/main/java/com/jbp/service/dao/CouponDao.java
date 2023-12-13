package com.jbp.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jbp.common.model.coupon.Coupon;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 优惠券表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-07-19
 */
public interface CouponDao extends BaseMapper<Coupon> {

    /**
     * 获取移动端优惠券列表
     * @param map 查询参数
     */
    List<Coupon> getH5ListBySearch(Map<String, Object> map);
}
